package com.github.blanexie.vxph.tracker

import com.github.blanexie.vxph.tracker.http.RouterLoadFactory
import com.github.blanexie.vxph.utils.setting
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.slf4j.LoggerFactory

class HttpServerVerticle(val port: Int) : CoroutineVerticle() {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val actionPackage = setting.getStr("vxph.http.action.package")

    override suspend fun start() {
        val httpServer = vertx.createHttpServer()
        val router = Router.router(vertx)
        val routerLoadFactory = RouterLoadFactory(actionPackage)
        routerLoadFactory.loadPathRouter(router)
        httpServer.requestHandler(router).listen(port)
        log.info("开始http服务， 端口：{}", port)
    }

}


fun main() {
    val httpServerVerticle = HttpServerVerticle(8086)
    val vertx = Vertx.vertx()
    vertx.deployVerticle(httpServerVerticle)
}