package com.github.blanexie.vxph.tracker

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.jdbcclient.JDBCPool
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.slf4j.LoggerFactory

class HttpServerVerticle(val port: Int) : CoroutineVerticle() {

    private val log = LoggerFactory.getLogger(this::class.java)

    override suspend fun start() {
        val httpServer = vertx.createHttpServer()
        val router = Router.router(vertx)
        buildRouter(router)
        httpServer.requestHandler(router).listen(port)
        log.info("开始http服务， 端口：{}", port)
    }


    private fun buildRouter(router: Router) {
        val announceAction = AnnounceAction()
        val path = announceAction::class.java.getAnnotation(Path::class.java)
        router.get(path.value).handler { ctx ->
            val request = ctx.request()
            val response = announceAction.process(request)
        }
    }

}


fun main() {
    val httpServerVerticle = HttpServerVerticle(8086)
    val vertx = Vertx.vertx()
    vertx.deployVerticle(httpServerVerticle)
}