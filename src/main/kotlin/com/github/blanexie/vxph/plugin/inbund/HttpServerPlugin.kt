package com.github.blanexie.vxph.plugin.inbund

import com.github.blanexie.vxph.core.AbstractVerticle
import com.github.blanexie.vxph.core.entity.Message
import io.vertx.core.Future
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory


class HttpServerPlugin(private val path: String, private val port: Int) :
  AbstractVerticle("httpServer", path, port.toString()) {

  private val log = LoggerFactory.getLogger(this::class.java)
  private val topicSet = mutableSetOf<String>()

  override suspend fun handleEnd() {
    val httpServer = vertx.createHttpServer()
    val router = buildRouter()
    httpServer.requestHandler(router).listen(port)
  }

  private suspend fun buildRouter(): Router {
    val router = Router.router(vertx)
    router.post(path)
      .respond { ctx ->
        val request = ctx.request()
        val receiver = getReceiver(request)
        if (topicSet.contains(receiver)) {
          request.bodyHandler {
            sendBodyMessage(receiver, it)
          }
          Future.succeededFuture(JsonObject.of("code", 200, "message", ""))
        } else {
          Future.succeededFuture(JsonObject.of("code", 404, "message", "not found flowId and id"))
        }
      }
    return router
  }

  private fun sendBodyMessage(receiver: String, buffer: Buffer) {
    launch {
      val currentTopic = getTopic()
      val message = Message(currentTopic, receiver, mapOf("body" to buffer.bytes.decodeToString()))
      sendMessage(message) {}
    }
  }

  private fun getReceiver(request: HttpServerRequest): String {
    val flowId = request.params()["flowId"]
    val id = request.params()["id"]
    return "http:$flowId:$id"
  }

  /**
   * 接受初始化消息
   */
  override suspend fun handleReceive(message: Message): Message {
    log.info("handleReceive message: $message")
    val topic = message.data["topic"] as String
    val method = message.data["method"] as String
    if (method == "add") topicSet.add(topic)
    if (method == "remove") topicSet.remove(topic)
    return Message(message.receiver, getTopic(), id = message.id)
  }
}
