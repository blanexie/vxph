package com.github.blanexie.vxph.torrent.util

import cn.hutool.core.io.FileTypeUtil

const val announceIntervalMinute = 10 * 60
const val peerActiveExpireMinute = announceIntervalMinute * 2

//started 种子开始下载
//completed 种子下载完成，开始做种
//stopped 种子停止下载 / 做种，不再活动
//empty 和没有此字段的情况完全相同
const val Event_Start = "started"
const val Event_Completed = "completed"
const val Event_Stopped = "stopped"
const val Event_Empty = "empty"


const val Announce_Url_Code = "Announce_Url_Code"
const val File_Allow_Suffix_Code = "File_Allow_Suffix"

enum class IpType {
    IPV4, IPV6
}


fun HashMap<String, Any>.readString(key: String): String {
    val byteBuffer = this[key] as java.nio.ByteBuffer?
    return byteBuffer?.let {
        String(it.array())
    } ?: ""
}