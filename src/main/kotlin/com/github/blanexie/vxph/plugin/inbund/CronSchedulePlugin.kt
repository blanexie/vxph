package com.github.blanexie.vxph.plugin.inbund

import com.github.blanexie.vxph.core.AbstractVerticle
import com.github.blanexie.vxph.core.entity.Message
import com.github.blanexie.vxph.core.entity.ReplyMessage

class CronSchedulePlugin(cron: String, name: String) :
  AbstractVerticle("cronSchedule", cron, name) {
  override suspend fun handleStart() {

  }

  override suspend fun handleEnd() {

  }

  override suspend fun handleReceive(message: Message): ReplyMessage {

  }


}
