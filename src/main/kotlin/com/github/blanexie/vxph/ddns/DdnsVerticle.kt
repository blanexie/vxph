package com.github.blanexie.vxph.ddns

import io.vertx.kotlin.coroutines.CoroutineVerticle


class DDNSVerticle : CoroutineVerticle() {

    val aliDnsService = AliDnsService(accessKey, accessKeySecret)
    override suspend fun start() {


    }

}

