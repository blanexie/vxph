package com.github.blanexie.vxph.core

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.blanexie.vxph.core.entity.Message
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

  protected suspend fun sendMessage(message: Message, handler: Handler<Message>) {
    val h = Handler<AsyncResult<io.vertx.core.eventbus.Message<String>>> {
      if (it.succeeded()) {
        val result = it.result()
        val replyMessage = result.body()
        log.info("receive reply message:$replyMessage  ")
        handler.handle(toBodyMessage(result))
      } else {
        throw it.cause()
      }
    }
    vertx.eventBus().send(message.receiver, objectMapper.writeValueAsString(message))
    log.info("send Message   receiver:${message.receiver}   sender:${message.sender}  id:${message.id}")
  }

  private suspend fun initConsumer() {
    val topic = getTopic()
    log.info("load localConsumer topic: $topic ")
    vertx.eventBus()
      .localConsumer(topic) { message ->
        log.info("receive message : $message ")
        launch {
          val result = handleReceive(toBodyMessage(message))
          result.type = "reply"
          log.info("reply message: $result ")
          message.reply(objectMapper.writeValueAsString(result))
        }
      }
  }

  abstract suspend fun handleStart()

  abstract suspend fun handleEnd()

  abstract suspend fun handleReceive(message: Message): Message

}


fun toBodyMessage(message: io.vertx.core.eventbus.Message<String>): Message {
  val body = message.body()
  val readValue = objectMapper.readValue(body, Map::class.java)

  return Message(
    readValue.get("receiver") as String, readValue.get("sender") as String,
    readValue.get("data") as Map<String, Any>,
    readValue.get("id") as Long, readValue.get("type") as String
  )

}
