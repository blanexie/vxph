package com.github.blanexie.vxph.dht.message

import com.github.blanexie.vxph.dht.KBucket
import com.github.blanexie.vxph.dht.bencode
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.socket.DatagramPacket
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress

abstract class BaseResponse(
    val t: String,
    val y: String,
    val r: Map<String, Any>,
    var ip4: InetSocketAddress? = null,
    var ip6: InetSocketAddress? = null,
) {

    private val log = LoggerFactory.getLogger(this::class.java)
    abstract fun apply(kBucket: KBucket)

    fun send(channel: Channel, kBucket: KBucket) {
        ip4?.let {
            val wrappedBuffer = Unpooled.wrappedBuffer(encode())
            val datagramPacket = DatagramPacket(wrappedBuffer, ip4, null)
            log.info("send data , remote addrss:{}  becode :{} ", datagramPacket.recipient(), this.toString())
            channel.writeAndFlush(datagramPacket).sync()
            kBucket.tCache.remove(t)
        }
        ip6?.let {
            val wrappedBuffer = Unpooled.wrappedBuffer(encode())
            val datagramPacket = DatagramPacket(wrappedBuffer, ip6, null)
            log.info("send data , remote addrss:{}  becode :{} ", datagramPacket.recipient(), this.toString())
            channel.writeAndFlush(datagramPacket).sync()
            kBucket.tCache.remove(t)
        }
    }

    private fun encode(): ByteArray {
        return bencode.encode(mapOf("t" to t, "y" to y, "r" to r))
    }

    override fun toString(): String {
        return "{ 't':$t , 'y':$y ,'r':$r,  'ip4':$ip4 , 'ip6':$ip6}"
    }


}