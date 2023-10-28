package com.github.blanexie.vxph.core.web

import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import org.slf4j.LoggerFactory

@Filter
class ExceptionFilter : HttpFilter {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun before(request: HttpServerRequest): Boolean {
        return true
    }

    override fun exception(request: HttpServerRequest, response: HttpServerResponse, e: Throwable): Boolean {
        log.error("filter exception, response http status is 500  , msg:{}", e.message)
        response.statusCode = 500
        response.statusMessage = "Server Error"
        response.send(e.message)
        return false
    }
}