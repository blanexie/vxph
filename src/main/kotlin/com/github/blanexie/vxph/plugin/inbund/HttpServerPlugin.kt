package com.github.blanexie.vxph.plugin.inbund

import com.github.blanexie.vxph.core.AbstractVerticle
import com.github.blanexie.vxph.core.entity.Message
import com.github.blanexie.vxph.core.entity.ReplyMessage
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import kotlinx.coroutines.launch


class HttpServerPlugin(private val path: String, private val port: Int) :
  AbstractVerticle("httpServer", path, port.toString()) {

  private val topicSet = mutableSetOf<String>()

  override suspend fun handleReceive(message: Message): ReplyMessage {
    val topic = message.data["topic"] as String
    topicSet.add(topic)
    return ReplyMessage(message.receiver, message.sender)
  }

  override suspend fun handleEnd() {
    val httpServer = vertx.createHttpServer()
    val router = buildRouter()
    httpServer.requestHandler(router).listen(port)
  }

  override suspend fun handleStart() {

  }


  private suspend fun buildRouter(): Router {
    val router = Router.router(vertx)
    router.post(path)
      .respond { ctx ->
        val request = ctx.request()
        val flowId = request.getParam("flowId")
        val id = request.getParam("id")
        val receiver = "http:$flowId:$id"
        if (topicSet.contains(receiver)) {
          val body = request.body()
          launch {
            val currentTopic = getTopic()
            val message = Message(currentTopic, receiver, mapOf("body" to body))
            sendMessage(message)
          }
          Future.succeededFuture(JsonObject.of("code", 200, "message", ""))
        } else {
          Future.succeededFuture(JsonObject.of("code", 404, "message", "not found flowId and id"))
        }
      }
    return router
  }

}
