package com.github.blanexie.vxph.plugin.outbund

import com.github.blanexie.vxph.core.AbstractVerticle
import com.github.blanexie.vxph.core.entity.Message
import com.github.blanexie.vxph.core.entity.MessageType
import io.vertx.core.Handler
import io.vertx.jdbcclient.JDBCConnectOptions
import io.vertx.jdbcclient.JDBCPool
import io.vertx.sqlclient.PoolOptions
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.Tuple
import io.vertx.sqlclient.templates.impl.SqlTemplate
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
    val options = JDBCConnectOptions()
      .setJdbcUrl(jdbcUrl)
      .setUser(username)
      .setPassword(password)
    val poolOptions = PoolOptions().setMaxSize(maxPoolSize).setName("pool-jdbc")
    pool = JDBCPool.pool(vertx, options, poolOptions)
  }

  override suspend fun handleReceive(message: Message, handler: Handler<Message>) {
    val sql = message.data["SQL"] as String
    val params = message.data["params"] as  List<*>

    val replyMessage = message.toReplyMessage()
    pool.preparedQuery(sql)
      .execute(Tuple.from(params))
      .onSuccess {
        val result = it.map(Row::toJson).toList()
        replyMessage.data = mapOf("result" to result)
        handler.handle(replyMessage)
      }.onFailure {
        log.info("jdbc error", it)
        replyMessage.data = mapOf("statusCode" to "500", "message" to it.message)
        handler.handle(replyMessage)
      }

  }


}
