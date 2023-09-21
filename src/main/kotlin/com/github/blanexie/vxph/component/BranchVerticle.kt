package com.github.blanexie.vxph.component

import com.github.blanexie.vxph.core.AbstractVerticle
import com.github.blanexie.vxph.core.entity.Line
import com.github.blanexie.vxph.core.entity.Message
import org.slf4j.LoggerFactory
import java.util.LinkedList
import kotlin.collections.LinkedHashSet

class BranchVerticle(flowId: String, id: String) : AbstractVerticle("brach", flowId, id) {

    private val log = LoggerFactory.getLogger(this::class.java)
    val input = mapOf("input" to LinkedList<Message>())

    val output = hashMapOf<String, Line>()

    override suspend fun handleReceiveSync(message: Message): Message {
        val type = message.data["type"] as String
        if (type == "addOutPutTopic") {
            val lineEnd = message.data["lineEnd"] as String
            val lineEndPoint = message.data["lineEndPoint"] as String
            val lineStartPoint = message.data["lineStartPoint"] as String
            output[lineStartPoint] = Line(lineStartPoint, lineEnd, lineEndPoint)
        }
        if (type == "removeOutPutTopic") {
            val lineEnd = message.data["lineEnd"] as String
            val lineEndPoint = message.data["lineEndPoint"] as String
            val lineStartPoint = message.data["lineStartPoint"] as String
            output.remove(lineStartPoint)

        }
        if (type == "message") {
            val lineEndPoint = message.data["lineEndPoint"] as String
            if (!input.contains(lineEndPoint)) {
                log.warn("$topic 收到消息但是无接受点， 消息丢弃，{}", message)
            } else {
                input[lineEndPoint]!!.add(message)
            }
        }
        sendOutMessage()

        return message.toReplyMessage()
    }

    private suspend fun sendOutMessage() {
        val messageLinkedList = input["input"]
        val removeLast = messageLinkedList!!.removeLast()
        for (it in output) {
            val value = it.value
            val copy = removeLast.copy()
            copy.sender = topic
            copy.receiver = value.end
            copy.data["lineEndPoint"] = value.endInPoint
            sendMessage(copy) {
            }
        }
    }

}
