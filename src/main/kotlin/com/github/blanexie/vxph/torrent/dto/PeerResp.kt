package com.github.blanexie.vxph.torrent.dto

import cn.hutool.core.convert.Convert
import cn.hutool.core.net.Ipv4Util
import cn.hutool.core.net.NetUtil
import cn.hutool.core.util.ByteUtil
import cn.hutool.core.util.HexUtil
import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.exception.VxphException
import com.github.blanexie.vxph.torrent.IpType
import java.nio.ByteBuffer

class PeerResp(
    val peerId: String,
    val ip: String,
    val port: Int,
    val type: IpType,
) {

    fun toBytes(): ByteArray {
        if (ip.contains(".")) {
            return toIpv4Bytes()
        } else if (ip.contains(":")) {
            return toIpv6Bytes()
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
        byteArray[4] = intToBytes[0]
        byteArray[5] = intToBytes[1]
        return byteArray
    }

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
        allocate.put(intToBytes[0])
        allocate.put(intToBytes[1])
        return allocate.array()
    }

}