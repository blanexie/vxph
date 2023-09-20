package com.github.blanexie.vxph.plugin.outbund

import com.github.blanexie.vxph.core.AbstractVerticle
import com.github.blanexie.vxph.core.entity.Message
import com.github.blanexie.vxph.core.objectMapper
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.jdbcclient.JDBCPool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.Tuple
import io.vertx.sqlclient.templates.SqlTemplate
import org.slf4j.LoggerFactory


class JdbcPlugin(
  val jdbcUrl: String,
  val username: String,
  val password: String,

  val maxPoolSize: Int = 16,
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
