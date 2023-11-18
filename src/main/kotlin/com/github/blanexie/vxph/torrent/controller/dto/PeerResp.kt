package com.github.blanexie.vxph.torrent.controller.dto

import cn.hutool.core.convert.Convert
import cn.hutool.core.util.ByteUtil
import cn.hutool.core.util.HexUtil
import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.exception.VxphException
import com.github.blanexie.vxph.torrent.util.IpType
import java.nio.ByteBuffer

data class PeerResp(
    val peerId: String,
    val ip: String,
    val port: Int,
    val type: IpType,
) {

    fun toMap(): Map<String, Any> {
        return mapOf("peer id" to peerId, "port" to port, "ip" to ip)
    }

    fun toBytes(): ByteArray {
        return if (IpType.IPV4 == type) {
            toIpv4Bytes()
        } else if (IpType.IPV6 == type) {
            toIpv6Bytes()
        } else {
            throw VxphException(SysCode.IpError)
        }
    }

    private fun toIpv4Bytes(): ByteArray {
        val byteArray = ByteArray(6)
        val split = ip.split(".")
        byteArray[0] = Convert.toByte(split[0])
        byteArray[1] = Convert.toByte(split[1])
        byteArray[2] = Convert.toByte(split[2])
        byteArray[3] = Convert.toByte(split[3])
        val intToBytes = ByteUtil.shortToBytes(port.toShort())
        byteArray[4] = intToBytes[1]
        byteArray[5] = intToBytes[0]
        return byteArray
    }

    //0100 0001      1111 0001       0100 0001
    private fun toIpv6Bytes(): ByteArray {
        val allocate = ByteBuffer.allocate(18)
        ip.split(":").forEach {
            if (it == "0") {
                allocate.put(0).put(0)
                return@forEach
            }
            val decodeHex = HexUtil.decodeHex(it)
            if (decodeHex.size == 1) {
                allocate.put(0).put(decodeHex)
            } else if (decodeHex.size == 2) {
                allocate.put(decodeHex)
            }
        }
        val intToBytes = ByteUtil.shortToBytes(port.toShort())
        allocate.put(intToBytes[1])
        allocate.put(intToBytes[0])
        return allocate.array()
    }

}