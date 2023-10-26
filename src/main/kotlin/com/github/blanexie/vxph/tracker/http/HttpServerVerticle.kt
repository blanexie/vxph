package com.github.blanexie.vxph.tracker.http

import cn.hutool.core.lang.Singleton
import cn.hutool.core.util.ReflectUtil
import cn.hutool.core.util.StrUtil
import com.github.blanexie.vxph.core.*
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.slf4j.LoggerFactory

@Verticle
class HttpServerVerticle : CoroutineVerticle() {

    private val log = LoggerFactory.getLogger(this::class.java)

    override suspend fun start() {
        val router = Router.router(vertx)
        loadPathRouter(router)
        val httpServer = vertx.createHttpServer()
        httpServer.requestHandler(router).listen(port)
        log.info("开始http服务， 端口：{}", port)
    }

    /**
     * 组装路由
     */
    private  fun loadPathRouter(router: Router) {
        val classAndMethods = findPathClass()
        classAndMethods.forEach {
            log.info("load router path:{} {}  class:{}   method:{}", it.reqMethod, it.path, it.clazz, it.method)
            val reqMethod = it.reqMethod
            val routerR = if (StrUtil.isEmpty(reqMethod)) {
                router.route(it.path)
            } else {
                router.route(HttpMethod.valueOf(reqMethod), it.path)
            }
            routerR.blockingHandler { r ->
                try {
                    val newInstance = Singleton.get(it.clazz)
                    val response: HttpServerResponse = ReflectUtil.invoke(newInstance, it.method, r.request())
                } catch (e: Throwable) {
                    log.error("path: {} request error", it.path, e)
                    r.response().statusCode = 500
                    r.response().putHeader("content-type", "text/plain; charset=utf-8 ")
                    r.response().send(e.message)
                }
            }
        }
    }


    /**
     * 找到所有的配置类
     */
    private fun findPathClass(): List<ClassAndMethod> {
        val ret = arrayListOf<ClassAndMethod>()
        annotationSet.forEach {
            val prefix = it.getAnnotation(Path::class.java).value
            it.methods.filter { m -> m.getAnnotation(Path::class.java) != null }.forEach { m ->
                val annotation = m.getAnnotation(Path::class.java)
                var path = "$prefix${annotation.value}".replace("//", "/").trim()
                if (path.endsWith("/")) {
                    path = path.removeSuffix("/")
                }
                ret.add(ClassAndMethod(it, m, path, annotation.method))
            }
        }
        return ret
    }

}
