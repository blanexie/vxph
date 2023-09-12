package com.github.blanexie.vxph.core

import com.github.blanexie.vxph.core.entity.Message
import com.github.blanexie.vxph.core.entity.ReplyMessage
import io.vertx.core.json.Json
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.awaitResult
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

abstract class AbstractVerticle(
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

  suspend fun getTopic(): String {
    return "$type:$flowId:$id"
  }

  protected suspend fun sendMessage(message: Message): Message {
    val reply = awaitResult<io.vertx.core.eventbus.Message<Message>> { h ->
      vertx.eventBus().request(message.receiver, message, h)
    }
    val body = reply.body()
    log.info("send Message:$message  ; receive ReplyMessage:$body")
    return body
  }

  private suspend fun initConsumer() {
    val topic = getTopic()
    vertx.eventBus()
      .localConsumer(topic) { message ->
        log.info("receive message : $message ")
        val body: String = message.body()
        val bodyJson = Json.decodeValue(body, Message::class.java)
        launch {
          val result = handleReceive(bodyJson)
          log.info("reply message: $result ")
          message.reply(Json.encode(result))
        }
      }
  }

  abstract suspend fun handleStart()

  abstract suspend fun handleEnd()

  abstract suspend fun handleReceive(message: Message): ReplyMessage

}
