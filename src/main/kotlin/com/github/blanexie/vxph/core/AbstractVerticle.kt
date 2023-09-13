package com.github.blanexie.vxph.core

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.blanexie.vxph.core.entity.Message
import com.github.blanexie.vxph.core.entity.MessageType
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

val objectMapper: ObjectMapper = ObjectMapper()

abstract class AbstractVerticle(
  private val type: String,
  private val flowId: String,
  private val id: String,
) :
  CoroutineVerticle() {

  private val log = LoggerFactory.getLogger(this::class.java)


  override suspend fun start() {
    log.info("${getTopic()} start......")
    this.handleStart()
    log.info("${getTopic()} handleStart OK ......")
    this.initConsumer()
    log.info("${getTopic()} initConsumer OK ......")
    this.handleEnd()
    log.info("${getTopic()} handleEnd OK ......")
  }

  suspend fun getTopic(): String {
    return "$type:$flowId:$id"
  }

  protected suspend fun sendMessage(message: Message, handler: Handler<Message>) {
    val h = Handler<AsyncResult<io.vertx.core.eventbus.Message<String>>> {
      if (it.succeeded()) {
        val result = it.result()
        val replyMessage = result.body()
        log.info("receive reply message: $replyMessage ")
        handler.handle(toBodyMessage(result))
      } else {
        throw it.cause()
      }
    }
    vertx.eventBus().request(message.receiver, objectMapper.writeValueAsString(message), h)
    log.info("sendMessage  message:$message")
  }

  private suspend fun initConsumer() {
    val topic = getTopic()
    vertx.eventBus()
      .localConsumer(topic) { message ->
        launch {
          val result = handleReceive(toBodyMessage(message))
          result.type = MessageType.reply
          log.info("reply message: $result")
          message.reply(objectMapper.writeValueAsString(result))
        }
      }
  }

  open suspend fun handleStart() {

  }

  open suspend fun handleEnd() {

  }

  open suspend fun handleReceive(message: Message): Message {
    log.info("handleReceive message: $message")
    return Message(message.receiver, getTopic(), id = message.id, type = MessageType.reply)
  }

}


fun toBodyMessage(message: io.vertx.core.eventbus.Message<String>): Message {
  val body = message.body()
  val readValue = objectMapper.readValue(body, Map::class.java)

  return Message(
    readValue["receiver"] as String, readValue["sender"] as String,
    readValue["data"] as Map<String, Any>,
    readValue["id"] as Long,
    MessageType.valueOf(readValue["type"] as String)
  )

}
