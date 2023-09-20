package com.github.blanexie.vxph

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.io.FileUtil
import cn.hutool.core.io.resource.ClassPathResource
import cn.hutool.core.util.CharsetUtil
import cn.hutool.core.util.ClassUtil
import cn.hutool.core.util.ReferenceUtil
import cn.hutool.core.util.ReflectUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.setting.SettingUtil
import com.github.blanexie.vxph.core.AbstractVerticle
import com.github.blanexie.vxph.core.entity.Message
import com.github.blanexie.vxph.core.objectMapper
import com.github.blanexie.vxph.plugin.outbund.JdbcPlugin
import io.vertx.core.Vertx
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import kotlin.reflect.full.valueParameters

/**
 * 启动三件事
 * 1. 加载框架verticle
 * 2. 加载插件
 * 3. 加载flow
 */
class MainVerticle : AbstractVerticle("mainVerticle", "0", "0") {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val setting = SettingUtil.get("vxph.properties")

    override suspend fun handleEnd() {
        loadJdbcPlugin()
        loadPluginVerticle()
    }

    private suspend fun loadPluginVerticle() {
        val receiver = getCoreJdbcPluginTopic();
        val message = Message(topic, receiver)
        val sql = "SELECT * FROM v_plugin WHERE status = 1"
        message.data = mapOf("sql" to sql, "params" to mapOf<String, Any>())
        sendMessage(message) {
            val rows = it.data["rows"] as List<*>
            if (CollUtil.isEmpty(rows)) {
                log.warn("数据库插件表中未找到插件， 无插件加载，需确认是否正常.......")
            } else {
                rows.forEach { row ->
                    val rowData = (row as Map<String, Any>)["map"] as Map<String, Any>
                    val pluginClass = rowData["class"] as String
                    val clazz = ClassUtil.loadClass<Class<*>>(pluginClass)

                    val args = rowData["args"] as String
                    val argsMap = objectMapper.readValue(args, Map::class.java)
                    val values = argsMap.values.stream().toArray()
                    val newInstance = ReflectUtil.newInstance(clazz, values)
                    println(newInstance)
                }
                //TODO 开始加载插件表
                log.info("插件加载完成.......")
            }
        }
    }

    private fun getCoreJdbcPluginTopic(): String {
        val jdbcUrl = setting.getStr("vxph.database.jdbc.url")
        val username = setting.getStr("vxph.database.username", "")
        return "jdbcPlugin:$jdbcUrl:$username"
    }

    private fun loadJdbcPlugin() {
        val receiver = getCoreJdbcPluginTopic();

        val jdbcUrl = setting.getStr("vxph.database.jdbc.url")
        val username = setting.getStr("vxph.database.username", "")
        val password = setting.getStr("vxph.database.password", "")
        val maxPoolSize = setting.getInt("vxph.database.maxPoolSize", 16)
        val jdbcPlugin = JdbcPlugin(jdbcUrl, username, password, maxPoolSize)
        vertx.deployVerticle(jdbcPlugin) {
            launch {
                if (it.succeeded()) {
                    checkAndInitSql(receiver)
                }
            }
        }
    }


    private suspend fun loadInitSql(receiver: String) {
        val ddlFile = setting.getStr("vxph.database.ddl.file", "vxph-ddl.sql")
        val classPathResource = ClassPathResource(ddlFile)
        val sqlLines = FileUtil.readString(classPathResource.file, CharsetUtil.CHARSET_UTF_8)
        StrUtil.split(sqlLines, ";").map { StrUtil.trim(it) }.filter { StrUtil.isNotBlank(it) }
            .forEach { sql ->
                val message = Message(topic, receiver)
                message.data = mapOf("sql" to sql, "params" to mapOf<String, Any>())
                sendMessage(message) {
                }
            }
    }

    private suspend fun checkAndInitSql(receiver: String) {
        val message = Message(topic, receiver)
        val sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='v_plugin'"
        message.data = mapOf("sql" to sql, "params" to mapOf<String, Any>())
        sendMessage(message) {
            launch {
                val rows = it.data["rows"] as List<*>
                if (CollUtil.isEmpty(rows)) {
                    log.info("核心数据库不存在， 开始初始化数据库.......")
                    loadInitSql(receiver)
                } else {
                    log.info("核心数据库已经存在.......")
                }
            }
        }
    }
}

fun main() {
    val vertx = Vertx.vertx()
    val abstractVerticle = MainVerticle()
    vertx.deployVerticle(abstractVerticle)
}

