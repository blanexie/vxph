package com.github.blanexie.vxph.core

import com.github.blanexie.vxph.core.entity.Message
import com.github.blanexie.vxph.core.entity.ReplyMessage
import io.vertx.core.json.Json
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.awaitResult
import org.slf4j.LoggerFactory

abstract class AbstractEventBusVerticle(
  private val flowId: String,
  private val id: String,
  private val type: String
) :
  CoroutineVerticle() {

  private val log = LoggerFactory.getLogger(this::class.java)

  override suspend fun start() {
    log.info("$type $flowId $id start......")
    this.handleStart()
    log.info("$type $flowId $id handleStart......")
    this.initConsumer()
    log.info("$type $flowId $id initConsumer......")
    this.handleEnd()
    log.info("$type $flowId $id handleEnd......")
  }

  protected suspend fun sendMessage(message: Message): Message {
    val reply = awaitResult<io.vertx.core.eventbus.Message<Message>> { h ->
      vertx.eventBus().request(message.receiver, message, h)
    }
    val body = reply.body()
    log.info("send Message:$message  ; receive ReplyMessage:$body")
    return body
  }

  private fun initConsumer() {
    val topic = "$type:$flowId:$id"
    vertx.eventBus()
      .localConsumer(topic) { message ->
        log.info("receive message : $message ")
        val body: String = message.body()
        val bodyJson = Json.decodeValue(body, Message::class.java)
        val result = this.handleReceive(bodyJson)
        log.info("reply message: $message ")
        message.reply(Json.encode(result))
      }
  }

  abstract fun handleEnd()

  abstract fun handleStart()

  abstract fun handleReceive(message: Message): ReplyMessage

}
