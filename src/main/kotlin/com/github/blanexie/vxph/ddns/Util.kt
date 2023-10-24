package com.github.blanexie.vxph.ddns

import com.github.blanexie.vxph.core.setting


val accessKey: String
    get():String {
        val property = System.getProperty("aliyun.accessKey")
        return property ?: setting.getStr("vxph.ddns.aliyun.accessKey")
    }

val accessKeySecret: String
    get():String {
        val property = System.getProperty("aliyun.accessKeySecret")
        return property ?: setting.getStr("vxph.ddns.aliyun.accessKeySecret")
    }

val scheduleMinutes = setting.getLong("vxph.ddns.aliyun.scheduleMinutes")

@FunctionalInterface
interface Handler<T, R> {
    fun handle(t: T?, r: R?)
}
