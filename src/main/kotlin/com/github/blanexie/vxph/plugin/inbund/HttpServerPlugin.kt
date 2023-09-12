package com.github.blanexie.vxph.plugin.inbund

import com.github.blanexie.vxph.core.entity.Message
import io.vertx.core.Future
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router

import io.vertx.kotlin.coroutines.CoroutineVerticle


class HttpServerPlugin(private val path: String, private val port: Int) : CoroutineVerticle() {

  private val topicSet = mutableSetOf<String>()

  override suspend fun start() {
    vertx.eventBus().localConsumer<String>("httpServer:$path:$port") { message ->
      val bodyJson = Json.decodeValue(message.body(), Message::class.java)
      val topic = bodyJson.data["topic"] as String
      topicSet.add(topic)
    }

    val httpServer = vertx.createHttpServer()
    val router = buildRouter()
    httpServer.requestHandler(router).listen(port)
  }


  private fun buildRouter(): Router {
    val router = Router.router(vertx)
    router.post(path)
      .respond { ctx ->
        val request = ctx.request()
        val flowId = request.getParam("flowId")
        val id = request.getParam("id")
        val topic = "http:$flowId:$id"
        if (topicSet.contains(topic)) {
          val body = request.body()
          vertx.eventBus().send(topic, body)
          Future.succeededFuture(JsonObject.of("code", 200, "message", ""))
        } else {
          Future.succeededFuture(JsonObject.of("code", 404, "message", "not found flowId and id"))
        }
      }
    return router
  }

}
