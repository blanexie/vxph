package com.github.blanexie.vxph.ddns.service

import cn.hutool.core.util.URLUtil
import cn.hutool.http.HttpUtil
import com.github.blanexie.vxph.core.contextMap
import com.github.blanexie.vxph.core.getVal
import io.vertx.core.Future
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.coroutines.awaitResult
import org.apache.hc.client5.http.utils.URIUtils
import org.slf4j.LoggerFactory
import java.net.http.HttpClient
import java.net.http.HttpRequest


class WebIpAddrServiceImpl : IpAddrService {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val httpClient = HttpClient.newBuilder().build()

    override fun ipv4(): String {
        val httpRequest = HttpRequest.newBuilder(URLUtil.toURI("https://ipv4.ddnspod.com")).build()
        val response = httpClient.send(httpRequest, java.net.http.HttpResponse.BodyHandlers.ofString())
        val body = response.body()
        log.info("获取到本机的ipv4地址为：{}", body)
        return body
    }

    override fun ipv6(): String {
        val httpRequest = HttpRequest.newBuilder(URLUtil.toURI("https://ipv6.ddnspod.com")).build()
        val response = httpClient.send(httpRequest, java.net.http.HttpResponse.BodyHandlers.ofString())
        val body = response.body()
        log.info("获取到本机的ipv6地址为：{}", body)
        return body
    }
}



