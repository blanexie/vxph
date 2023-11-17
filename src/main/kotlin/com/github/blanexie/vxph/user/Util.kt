package com.github.blanexie.vxph.user

import com.dampcake.bencode.Type
import com.github.blanexie.vxph.common.bencode
import java.io.ByteArrayOutputStream
import java.io.File


class Util


//登录的传入time过期时间
const val LoginTimeExpireMS = 60 * 60 * 1000


fun main() {
    val torrentMap = hashMapOf<String, Any>()
//    torrentMap["comment"] = "torrent.comment"
//    torrentMap["create date"] = System.currentTimeMillis() / 1000
//    torrentMap["create by"] = "torrent.createdBy"
//    torrentMap["announce"] = "http://localhost:8018/announce"
    torrentMap["info"] = "info"
    val torrentBytes = bencode.encode(torrentMap)
    val infoBytes =
        File("C:/Users/76515/IdeaProjects/vxph/db/torrent/7be75e961b967ed43561ddd426eb66ca9c402e1b").readBytes()
    val out = ByteArrayOutputStream()
    out.write(torrentBytes, 0, torrentBytes.size - 1)
    out.write(byteArrayOf(0x34, 0x3a, 0x69, 0x6e, 0x66, 0x6f))
    out.write(byteArrayOf(0x34, 0x3a, 0x69, 0x6e, 0x66, 0x6f))
    out.write(0x65)
    val bytes = out.toByteArray()
    val decode = bencode.decode(bytes, Type.DICTIONARY)
    println(decode)
}
