package com.github.blanexie.vxph.ddns.service

import cn.hutool.http.HttpUtil
import org.slf4j.LoggerFactory
import java.net.HttpURLConnection
import java.net.URL


class LocalIpAddrServiceImpl : IpAddrService {

    private val log = LoggerFactory.getLogger(this::class.java)
    override fun ipv4(): String {
        val result = HttpUtil.createGet("https://ipv4.ddnspod.com").execute()
        return result.body()
    }

    override fun ipv6(): String {
        val result = HttpUtil.createGet("https://ipv4.ddnspod.com").execute()
        return result.body()
    }
}

