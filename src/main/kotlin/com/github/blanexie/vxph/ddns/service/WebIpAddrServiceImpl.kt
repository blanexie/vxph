package com.github.blanexie.vxph.ddns.service

import cn.hutool.http.HttpUtil
import org.slf4j.LoggerFactory


class WebIpAddrServiceImpl : IpAddrService {

    private val log = LoggerFactory.getLogger(this::class.java)
    override fun ipv4(): String {
        val result = HttpUtil.createGet("https://ipv4.ddnspod.com").execute()
        val body = result.body()
        log.info("获取到本机的ipv4地址为：{}", body)
        return body
    }

    override fun ipv6(): String {
        val result = HttpUtil.createGet("https://ipv6.ddnspod.com").execute()
        val body = result.body()
        log.info("获取到本机的ipv6地址为：{}", body)
        return body
    }
}


fun main(){
    var webIpAddrServiceImpl = WebIpAddrServiceImpl()
    var ipv6 = webIpAddrServiceImpl.ipv6()
    println(ipv6)
}

