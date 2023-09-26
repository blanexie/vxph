package com.github.blanexie.vxph.core

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.blanexie.vxph.core.entity.Line
import com.github.blanexie.vxph.core.entity.Message
import com.github.blanexie.vxph.core.entity.MessageType
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.awaitResult
import io.vertx.kotlin.ext.sql.resultSetOf
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.util.*

val objectMapper: ObjectMapper = ObjectMapper()


abstract class AbstractVerticle(type: String, flowId: String, id: String) : CoroutineVerticle() {

    private val log = LoggerFactory.getLogger(this::class.java)

    val topic = "$type:$flowId:$id"
    val input = hashMapOf<String, LinkedList<Message>>()
    val output = hashMapOf<String, Line>()

    fun initPoint(pointMap: Map<String, *>) {
        val inputPoint = pointMap["input"] as List<*>
        inputPoint.filterNotNull()
            .map { it as String }
            .forEach {
                input[it] = LinkedList<Message>()
            }

        val outputPoint = pointMap["output"] as Map<*, *>
        outputPoint.forEach { (k, v) ->
            k as String
            val lineMap = v as Map<*, *>
            val line = Line(lineMap["outPoint"] as String, lineMap["end"] as String, lineMap["endInPoint"] as String)
            output[k] = line
        }
    }

    override suspend fun start() {
        this.handleStart()
        this.initConsumer()
        this.handleEnd()
        log.info("$topic verticle start OK ......")
    }

    protected suspend fun sendMessage(message: Message, handler: Handler<Message>) {
        val h = Handler<AsyncResult<io.vertx.core.eventbus.Message<String>>> {
            if (it.succeeded()) {
                val result = it.result()
                handler.handle(toBodyMessage(result))
            } else {
                throw it.cause()
            }
        }
        vertx.eventBus().request(message.receiver, objectMapper.writeValueAsString(message), h)
        log.info("sendMessage  message:$message")
    }

    protected suspend fun sendMessageSync(message: Message): Message {
        val replyMessage: io.vertx.core.eventbus.Message<String> = awaitResult {
            vertx.eventBus().request(message.receiver, objectMapper.writeValueAsString(message), it)
        }
        return toBodyMessage(replyMessage)
    }

    private suspend fun initConsumer() {
        vertx.eventBus().localConsumer(topic) { message ->
            launch {
                handleReceive(toBodyMessage(message)) {
                    it.type = MessageType.reply
                    message.reply(objectMapper.writeValueAsString(it))
                }
            }
        }
    }

    open suspend fun handleStart() {

    }

    open suspend fun handleEnd() {

    }

    open suspend fun handleReceive(message: Message, handler: Handler<Message>) {
        val replyMessage = handleReceiveSync(message)
        handler.handle(replyMessage)
    }

    open suspend fun handleReceiveSync(message: Message): Message {
        log.info("handleReceive message: $message")
        return message.toReplyMessage()
    }

    @Suppress("UNCHECKED_CAST")
    private fun toBodyMessage(message: io.vertx.core.eventbus.Message<String>): Message {
        val body = message.body()
        val readValue = objectMapper.readValue(body, Map::class.java)
        return Message(
            readValue["receiver"] as String, readValue["sender"] as String,
            readValue["data"] as Map<String, Any>,
            readValue["id"] as Long,
            MessageType.valueOf(readValue["type"] as String)
        )
    }
}
