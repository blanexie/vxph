package com.github.blanexie.vxph.core.web

import cn.hutool.core.util.ReflectUtil
import com.github.blanexie.vxph.core.*
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.slf4j.LoggerFactory


abstract class HttpVerticle : CoroutineVerticle() {
    private val log = LoggerFactory.getLogger(this::class.java)

    override suspend fun start() {
        enablePathRouter()
    }


    protected fun enablePathRouter() {
        val pathDefines = findPathClass()
        pathDefines.forEach {
            log.info("load router path:{} {} method:{}   ", it.reqMethod, it.path, it.method.name)
            val router: Router = contextMap.getIfAbsent("router") { Router.router(vertx) }
            router.route(HttpMethod.valueOf(it.reqMethod), it.path).blockingHandler { r ->
                val request = r.request()
                val response = r.response()
                try {
                    if (!invokeFilterBefore(request)) {
                        r.respFail(403, Error("filter before invoke fun return false"))
                        return@blockingHandler
                    }
                    val invoke: Any? = ReflectUtil.invoke<Any>(this, it.method, request)
                    invoke?.let { result ->
                        if (result is String) {
                            response.send(result)
                        } else {
                            response.send(objectMapper.writeValueAsString(result))
                        }
                    }
                } catch (e: Throwable) {
                    invokeFilterException(request, response, e)
                    log.error("处理{}路径请求异常", it.path, e)
                }
            }
        }
    }

    private fun invokeFilterException(request: HttpServerRequest, response: HttpServerResponse, e: Throwable): Boolean {
        val httpFilters: List<HttpFilter>? = contextMap.getVal("httpFilters")
        httpFilters?.forEach { filter ->
            if (!filter.exception(request, response, e)) {
                return false
            }
        }
        return true
    }

    private fun invokeFilterBefore(request: HttpServerRequest): Boolean {
        val httpFilters: List<HttpFilter>? = contextMap.getVal("httpFilters")
        httpFilters?.forEach { filter ->
            if (!filter.before(request)) {
                return false
            }
        }
        return true
    }

    private fun findPathClass(): List<PathDefine> {
        val clazz = this::class.java
        val pathDefines = clazz.methods.mapNotNull { m ->
            val annotation = m.getAnnotation(Path::class.java)
            annotation?.let {
                var path = annotation.value.replace("//", "/").trim()
                if (path.endsWith("/")) {
                    path = path.removeSuffix("/")
                }
                PathDefine(m, path, annotation.method)
            }
        }
        return pathDefines
    }
}
