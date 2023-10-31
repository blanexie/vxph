package com.github.blanexie.vxph.core.web

import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory

@Filter
class ExceptionFilter : HttpFilter {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun before(ctx:RoutingContext): Boolean {
        return true
    }

    override fun exception(ctx:RoutingContext, e: Throwable): Boolean {
        val response = ctx.response()
        log.error("filter exception, response http status is 500  , msg:{}", e.message)
        response.statusCode = 500
        response.statusMessage = "Server Error"
        response.send(e.message)
        return false
    }
}