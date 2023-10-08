package com.github.blanexie.vxph.dht.message

import cn.hutool.core.util.IdUtil
import com.github.blanexie.vxph.dht.*
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.socket.DatagramPacket
import org.slf4j.LoggerFactory
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetSocketAddress

abstract class BaseRequest(
    val t: String,
    val y: String,
    val q: String,
    val a: Map<String, Any>,
    var ip4: InetSocketAddress? = null,
    var ip6: InetSocketAddress? = null,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    abstract fun apply(kBucket: KBucket): BaseResponse

    private fun encode(): ByteArray {
        return bencode.encode(mapOf("t" to t, "y" to y, "q" to q, "a" to a))
    }

    fun send(channel: Channel, kBucket: KBucket) {
        ip4?.let {
            val wrappedBuffer = Unpooled.wrappedBuffer(encode())
            val datagramPacket = DatagramPacket(wrappedBuffer, ip4, null)
            log.info("send data , remote addrss:{}  becode :{} ", datagramPacket.recipient(), this.toString())
            channel.writeAndFlush(datagramPacket).sync()
            kBucket.tCache.put(t, TReqInfo(t, q, ip4))
        }
        ip6?.let {
            val wrappedBuffer = Unpooled.wrappedBuffer(encode())
            val datagramPacket = DatagramPacket(wrappedBuffer, ip6, null)
            log.info("send data , remote addrss:{}  becode :{} ", datagramPacket.recipient(), this.toString())
            channel.writeAndFlush(datagramPacket).sync()
            kBucket.tCache.put(t, TReqInfo(t, q, null, ip6))
        }
    }


    override fun toString(): String {
        return "{ 't':$t , 'y':$y ,'q':$q, 'a': $a , 'ip4':$ip4 , 'ip6':$ip6}"
    }


}