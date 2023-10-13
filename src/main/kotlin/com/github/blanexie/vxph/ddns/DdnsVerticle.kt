package com.github.blanexie.vxph.ddns

import io.vertx.kotlin.coroutines.CoroutineVerticle


class DDNSVerticle : CoroutineVerticle() {

    val aliDnsService = AliDNSService(accessKey, accessKeySecret)

    override suspend fun start() {
        vertx.setPeriodic(15){

        }

    }

}

