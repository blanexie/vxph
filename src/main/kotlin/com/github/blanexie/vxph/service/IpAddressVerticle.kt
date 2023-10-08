package com.github.blanexie.vxph.service

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.util.IdUtil
import cn.hutool.crypto.digest.DigestUtil
import com.github.blanexie.vxph.core.AbstractVerticle
import com.github.blanexie.vxph.dht.bencode
import org.slf4j.LoggerFactory
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress


/**
 * ip地址获取， 并发送邮件
 */
class IpAddressVerticle() : AbstractVerticle("", "", "") {

    private val log = LoggerFactory.getLogger(this::class.java)

    private var ipSet: List<IpAddressLocal>? = null

    override suspend fun handleEnd() {
        vertx.setPeriodic(5 * 1000) {
            getIpsAndSendEmail()
        }
    }

    private fun getIpsAndSendEmail() {
        val localHosts = InetAddress.getAllByName(InetAddress.getLocalHost().hostName)

        val ipAddressLocals = localHosts.map {
            IpAddressLocal(it.hostAddress, it.hostName, it.isReachable(5000))
        }.toList()

        if (CollUtil.isEqualList(ipAddressLocals, ipSet)) {
            log.info("ip地址没变， {}", ipSet)
        } else {
            ipSet = ipAddressLocals
            // MailUtil.se()
            //发送邮件
            //sendMessage()
            log.info("发送邮件 {}", ipSet)
        }
    }
}


data class IpAddressLocal(
    val ip: String,
    val macName: String,
    val isReadable: Boolean
)


fun main() {
    //router.bittorrent.com:6881
    //router.utorrent.com:6881
    //dht.transmissionbt.com:6881
    //dht.aelitis.com
    DatagramSocket(10087).use { socket ->
        val datagramPacket = DatagramPacket(ByteArray(1024), 1024)
        val mapOf = mapOf(
            "t" to IdUtil.fastUUID(),
            "y" to "q",
            "q" to "find_node",
            "a" to mapOf("id" to DigestUtil.sha1("124zxc"), "target" to DigestUtil.sha1("zx124c1"))
        )
        val encode = bencode.encode(mapOf)
        val packet = DatagramPacket(encode, encode.size, InetSocketAddress("router.utorrent.com", 6881))
        socket.send(packet)

        while (true) {
            socket.receive(datagramPacket)
            println(datagramPacket)
        }
    }
}
