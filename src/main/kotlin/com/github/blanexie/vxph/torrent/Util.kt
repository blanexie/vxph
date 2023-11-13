package com.github.blanexie.vxph.torrent

import cn.hutool.core.net.Ipv4Util
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.dampcake.bencode.Bencode
import java.net.InetSocketAddress

class Util {
}

//started, completed或stopped之一
const val Event_Start = "started"
const val Event_Completed = "completed"
const val Event_Stopped = "stopped"
const val Event_Empty = "empty"


fun convertPortToBytes(value: Int): ByteArray {
    val byteArray = byteArrayOf(
        (value shr 16).toByte(), (value shr 24).toByte()
    )
    return byteArray
}


