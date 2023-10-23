package com.github.blanexie.vxph.tracker.http

import com.github.blanexie.vxph.core.PathRouterProcess
import com.github.blanexie.vxph.core.Verticle
import com.github.blanexie.vxph.ddns.DDNSVerticle
import com.github.blanexie.vxph.core.port
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.slf4j.LoggerFactory

@Verticle
class HttpServerVerticle : CoroutineVerticle() {

    private val log = LoggerFactory.getLogger(this::class.java)


    override suspend fun start() {
        val pathRouterProcess = PathRouterProcess()
        val router = Router.router(vertx)
        pathRouterProcess.loadPathRouter(router)

        val httpServer = vertx.createHttpServer()
        httpServer.requestHandler(router).listen(port)
        log.info("开始http服务， 端口：{}", port)
    }

}
