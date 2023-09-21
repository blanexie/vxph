package com.github.blanexie.vxph.plugin.outbund

import cn.hutool.core.io.FileUtil
import cn.hutool.core.io.resource.ClassPathResource
import cn.hutool.core.util.CharsetUtil
import cn.hutool.core.util.StrUtil
import com.github.blanexie.vxph.core.AbstractVerticle
import com.github.blanexie.vxph.core.entity.Message
import com.github.blanexie.vxph.core.objectMapper
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.jdbcclient.JDBCPool
import io.vertx.kotlin.coroutines.awaitResult
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.templates.SqlTemplate
import org.slf4j.LoggerFactory


class JdbcPlugin(
    val jdbcUrl: String,
    val username: String,
    val password: String,
    val maxPoolSize: Int = 16,
    val isCoreJdbc: Boolean = false,
) :
    AbstractVerticle(type = "jdbcPlugin", flowId = jdbcUrl, id = username) {

    private val log = LoggerFactory.getLogger(this::class.java)

    private lateinit var pool: JDBCPool

    override suspend fun handleStart() {
        val config = JsonObject()
            .put("url", jdbcUrl)
            .put("datasourceName", "main")
            .put("user", username)
            .put("password", password)
            .put("max_pool_size", maxPoolSize)
        pool = JDBCPool.pool(vertx, config)
        if (isCoreJdbc) {
            initCoreDB()
        }
    }


    /**
     * 初始化核心数据库
     */
    private suspend fun initCoreDB() {
        //检查核心数据库是否存在

        val rowSet = awaitResult {
            val sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='v_plugin'"
            SqlTemplate.forQuery(pool, sql).execute(mapOf())
                .onComplete(it)
        }

        val rowCount = rowSet.size()
        if (rowCount > 0) {
            log.info(" core db exist .......................")
            return
        }
        //初始化sql文件地址是固定的
        val classPathResource = ClassPathResource("vxph-ddl.sql")
        val sqlLines = FileUtil.readString(classPathResource.file, CharsetUtil.CHARSET_UTF_8)
        StrUtil.split(sqlLines, ";").map { StrUtil.trim(it) }.filter { StrUtil.isNotBlank(it) }
            .forEach { sql ->
                log.info("=== SQL :$sql")
                log.info("=== params :  ")
                val s = awaitResult {
                    SqlTemplate.forUpdate(pool, sql).execute(mapOf())
                        .onComplete(it)
                }
            }
    }


    override suspend fun handleReceive(message: Message, handler: Handler<Message>) {
        val sql = message.data["sql"] as String
        val params = message.data["params"] as Map<String, *>
        log.info("=== SQL :$sql")
        log.info("=== params :${objectMapper.writeValueAsString(params)}")
        val replyMessage = message.toReplyMessage()
        SqlTemplate.forQuery(pool, sql)
            .execute(params)
            .onSuccess {
                val result = it.map(Row::toJson).toList()
                replyMessage.data = mapOf("rows" to result, "statusCode" to 200)
                handler.handle(replyMessage)
            }.onFailure {
                log.info("jdbc error", it)
                replyMessage.data = mapOf("statusCode" to 500, "message" to it.message)
                handler.handle(replyMessage)
            }
    }

}
