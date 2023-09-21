package com.github.blanexie.vxph.core

import cn.hutool.core.collection.CollUtil
import com.github.blanexie.vxph.core.entity.Message
import com.github.blanexie.vxph.plugin.PluginFactory
import com.github.blanexie.vxph.plugin.getCoreJdbcPluginTopic
import com.github.blanexie.vxph.plugin.pluginFactory
import org.slf4j.LoggerFactory


class PluginLoadVerticle : AbstractVerticle(type = "pluginLoad", flowId = "pluginLoad", id = "pluginLoad") {

    private val log = LoggerFactory.getLogger(this::class.java)

    override suspend fun handleEnd() {
        loadPlugin()
    }

    private suspend fun loadPlugin() {
        val receiver = getCoreJdbcPluginTopic()
        val message = Message(topic, receiver)
        val sql = "SELECT * FROM v_plugin WHERE status = 1"
        message.data = mapOf("sql" to sql, "params" to mapOf<String, Any>())
        sendMessage(message) {
            val rows = it.data["rows"] as List<*>
            if (CollUtil.isEmpty(rows)) {
                log.warn("数据库插件表中未找到插件， 无插件加载，需确认是否正常.......")
                return@sendMessage
            }

            rows.map { row ->
                val rowData = (row as Map<*, *>)["map"] as Map<*, *>
                pluginFactory.build(rowData)
            }.forEach { abstractVerticle ->
                vertx.deployVerticle(abstractVerticle) { dr ->
                    if (dr.succeeded()) log.info(" topic: {} 加载完成", abstractVerticle.topic)
                    else log.error(" topic: {} 加载异常， ", abstractVerticle.topic, dr.cause())
                }
            }
            log.info("插件加载完成.......")
        }

    }


}
