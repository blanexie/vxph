package com.github.blanexie.vxph.ddns

import com.github.blanexie.vxph.core.setting


val accessKey: String
  get():String {
    System.getenv()
    return setting.getStr("vxph.ddns.aliyun.accessKey", System.getProperty("aliyun.accessKey"))
  }

val accessKeySecret: String
  get():String {
    System.getenv()
    return setting.getStr("vxph.ddns.aliyun.accessKeySecret", System.getProperty("aliyun.accessKeySecret"))
  }

val scheduleMinutes = setting.getLong("vxph.ddns.aliyun.scheduleMinutes")

@FunctionalInterface
interface Handler<T, R> {
  fun handle(t: T?, r: R?)
}
