package com.github.blanexie.vxph.core.web

import cn.hutool.core.util.ClassUtil
import com.github.blanexie.vxph.core.Verticle
import com.github.blanexie.vxph.core.contextMap
import com.github.blanexie.vxph.core.event.*
import com.github.blanexie.vxph.core.getProperty
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.slf4j.LoggerFactory

@Verticle
class HttpVerticle : CoroutineVerticle() {
    private val log = LoggerFactory.getLogger(this::class.java)

    private val port = getProperty("vxph.http.server.port", 8016)

    override suspend fun start() {
        val eventBus = vertx.eventBus()
        val router = Router.router(vertx)
        eventBus.consumer(CoreEventChannel) {
            val event = it.toEvent()
            log.info("receive Event,  {} {} {}", event.channel, event.type, event.data)
            if (event.type == VerticleLoadCompleteEventType) {
                val httpServer = vertx.createHttpServer()
                val packagePath = event.data
                loadPathRouter(packagePath!!, router)
                httpServer.requestHandler(router).listen(port)
                log.info("开始http服务， 端口：{}", port)
            }
        }
        log.info("HttpVerticle start fun end")
    }

    /**
     * 组装路由
     */
    private fun loadPathRouter(packagePath: String, router: Router) {
        val pathDefines = findPathClass(packagePath)
        pathDefines.forEach {
            contextMap[it.clazz.name] = it.newInstance()
            log.info("load router path:{} {} method:{}  class:{} ", it.reqMethod, it.path, it.method, it.clazz)
            router.route(HttpMethod.valueOf(it.reqMethod), it.path)
                .blockingHandler { r ->
                    try {
                        it.invoke(r.request())
                    } catch (e: Throwable) {
                        r.respFail(500, e)
                        log.error("处理{}路径请求异常", it.path, e)
                    }
                }
        }
    }

    /**
     * 找到所有的配置类
     */
    private fun findPathClass(packagePath: String): List<PathDefine> {
        val classSet = ClassUtil.scanPackageByAnnotation(packagePath, Path::class.java)
        val pathDefines = classSet.map { clazz ->
            val prefix = clazz.getAnnotation(Path::class.java).value
            clazz.methods.mapNotNull { m ->
                val annotation = m.getAnnotation(Path::class.java)
                annotation?.let {
                    var path = "$prefix${annotation.value}".replace("//", "/").trim()
                    if (path.endsWith("/")) {
                        path = path.removeSuffix("/")
                    }
                    PathDefine(clazz, m, path, annotation.method)
                }
            }
        }.flatMap {
            it.toList()
        }.toList()
        return pathDefines
    }

}


fun RoutingContext.respFail(code: Int, e: Throwable) {
    val response = this.response()
    response.statusCode = code
    response.putHeader("content-type", "text/plain; charset=utf-8 ")
    response.send(e.message)
}