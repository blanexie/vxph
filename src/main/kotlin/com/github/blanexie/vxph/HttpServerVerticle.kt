package com.github.blanexie.vxph

import com.github.blanexie.vxph.ddns.DDNSVerticle
import com.github.blanexie.vxph.tracker.http.RouterLoadFactory
import com.github.blanexie.vxph.utils.port
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.slf4j.LoggerFactory

class HttpServerVerticle : CoroutineVerticle() {

    private val log = LoggerFactory.getLogger(this::class.java)

    override suspend fun start() {
        vertx.deployVerticle(DDNSVerticle())
        val httpServer = vertx.createHttpServer()
        val router = Router.router(vertx)
        val routerLoadFactory = RouterLoadFactory(this::class.java.packageName)
        routerLoadFactory.loadPathRouter(router)
        httpServer.requestHandler(router).listen(port)
        log.info("开始http服务， 端口：{}", port)
    }

}


fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(HttpServerVerticle())
}