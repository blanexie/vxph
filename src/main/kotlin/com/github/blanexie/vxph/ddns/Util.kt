package com.github.blanexie.vxph.ddns

import com.github.blanexie.vxph.core.getProperty


val accessKey: String = getProperty("vxph.ddns.aliyun.accessKey")!!
val accessKeySecret: String = getProperty("vxph.ddns.aliyun.accessKeySecret")!!
val scheduleMinutes = getProperty("vxph.ddns.aliyun.scheduleMinutes", 15)
