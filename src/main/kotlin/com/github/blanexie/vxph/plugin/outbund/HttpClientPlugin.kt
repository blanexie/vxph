package com.github.blanexie.vxph.plugin.outbund

import com.github.blanexie.vxph.core.AbstractVerticle
import com.github.blanexie.vxph.core.entity.Message
import io.vertx.core.Handler
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import org.slf4j.LoggerFactory

class HttpClientPlugin(name: String) :
    AbstractVerticle(type = "httpClient", flowId = name, id = "_") {

    private val log = LoggerFactory.getLogger(this::class.java)

    private lateinit var client: WebClient

    override suspend fun handleStart() {
        val options = WebClientOptions()
        options.setKeepAlive(false)
        client = WebClient.create(vertx, options);
    }

    override suspend fun handleReceive(message: Message, handler: Handler<Message>) {
        val data = message.data
        val url = data["url"] as String
        val method = data["method"] as String
        val header = data["header"] as Map<String, String>
        val body = data["body"] as String

        val request = client.request(
            HttpMethod.valueOf(method.uppercase()),
            url
        )
        header.forEach { (k, v) ->
            request.putHeader(k, v)
        }
        val replyMessage = message.toReplyMessage()
        val responseFuture = request.sendJson(body)
        responseFuture.onSuccess {
            val bodyAsString = it.bodyAsString()
            replyMessage.data = mapOf("body" to bodyAsString, "statusCode" to it.statusCode())
            handler.handle(replyMessage)
        }.onFailure {
            log.error("http request fail ", it)
            replyMessage.data = mapOf("statusCode" to 500, "errorMsg" to it.message)
            handler.handle(replyMessage)
        }
    }

}
