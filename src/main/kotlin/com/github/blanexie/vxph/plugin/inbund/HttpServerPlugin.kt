package com.github.blanexie.vxph.plugin.inbund

import com.github.blanexie.vxph.core.AbstractVerticle
import com.github.blanexie.vxph.core.entity.Message
import io.vertx.core.Future
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

  override suspend fun handleStart() {

  }


  private suspend fun buildRouter(): Router {
    val router = Router.router(vertx)
    router.post(path)
      .respond { ctx ->
        val request = ctx.request()
        val flowId = request.getParam("flowId")
        val id = request.getParam("id")
        val type = request.getParam("type")

        val receiver = "$type:$flowId:$id"
        if (topicSet.contains(receiver)) {
          request.bodyHandler { buffer ->
            launch {
              val currentTopic = getTopic()
              val message = Message(currentTopic, receiver, mapOf("body" to buffer.bytes.decodeToString()))
              sendMessage(message) {}
            }
          }
          Future.succeededFuture(JsonObject.of("code", 200, "message", ""))
        } else {
          Future.succeededFuture(JsonObject.of("code", 404, "message", "not found flowId and id...."))
        }
      }
    return router
  }

  /**
   * 接受初始化消息
   */
  override suspend fun handleReceive(message: Message): Message {
    log.info("receive message: $message")
    val topic = message.data["topic"] as String
    val method = message.data["method"] as String
    if (method == "add") topicSet.add(topic)
    if (method == "remove") topicSet.remove(topic)
    return Message(message.receiver, getTopic(), id = message.id)
  }
}
