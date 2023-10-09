package com.github.blanexie.vxph.tracker

import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineVerticle

class HttpServerVerticle(val port: Int) : CoroutineVerticle() {

    override suspend fun start() {
        val httpServer = vertx.createHttpServer()
        val router = Router.router(vertx)
      //  buildRouter()
        httpServer.requestHandler(router).listen(port)
    }

    private suspend fun buildRouter(path: String, router: Router) {
        router.post(path)
            .respond { ctx ->
                val request = ctx.request()

             //   val receiver = getReceiver(request)

             //   request.bodyHandler { sendBodyMessage(receiver, it) }
                Future.succeededFuture(JsonObject.of("code", 200, "message", ""))

                Future.succeededFuture(JsonObject.of("code", 404, "message", "not found flowId and id"))

            }
    }

}