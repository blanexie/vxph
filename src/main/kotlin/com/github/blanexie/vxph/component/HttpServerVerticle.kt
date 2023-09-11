package com.github.blanexie.vxph.component

import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineVerticle


class HttpServerVerticle : CoroutineVerticle() {

  override suspend fun start() {
    val httpServer = vertx.createHttpServer()
    val router = Router.router(vertx)
    router.post("/api/send")
      .respond { ctx ->
        val request = ctx.request()
        val topic = request.getParam("topic")
        val body = request.body()
        vertx.eventBus().send(topic, body)
        Future.succeededFuture(JsonObject.of("code", 200, "message", ""))
      }
    httpServer.requestHandler(router).listen(8088)
  }

}
