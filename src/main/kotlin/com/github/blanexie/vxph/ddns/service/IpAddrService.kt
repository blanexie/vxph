package com.github.blanexie.vxph.ddns.service

import io.vertx.sqlclient.Tuple


/**
 * 获取ip地址的方式
 */
interface IpAddrService {


    fun ipv4(): String
    fun ipv6(): String


}