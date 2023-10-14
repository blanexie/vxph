package com.github.blanexie.vxph.ddns

import cn.hutool.core.lang.Singleton
import com.github.blanexie.vxph.ddns.controller.DdnsAction
import io.vertx.kotlin.coroutines.CoroutineVerticle


class DDNSVerticle : CoroutineVerticle() {

    val ddnsAction = Singleton.get(DdnsAction::class.java)

    override suspend fun start() {
        vertx.setPeriodic(scheduleMinutes * 60 * 1000) {
            ddnsAction.scheduleUpdateIpRecord()
        }
    }

}

