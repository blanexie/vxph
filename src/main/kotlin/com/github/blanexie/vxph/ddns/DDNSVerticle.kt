package com.github.blanexie.vxph.ddns

import cn.hutool.core.lang.Singleton
import com.github.blanexie.vxph.core.Verticle
import com.github.blanexie.vxph.core.contextMap
import com.github.blanexie.vxph.core.getVal
import com.github.blanexie.vxph.ddns.controller.DDNSAction
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.launch

@Verticle
class DDNSVerticle : CoroutineVerticle() {

    override suspend fun start() {
        vertx.setPeriodic(scheduleMinutes * 60 * 1000L) {
            val ddnsAction: DDNSAction? = contextMap.getVal(DDNSAction::class.java.name)
            ddnsAction?.schedule()
        }
    }
}



