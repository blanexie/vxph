package com.github.blanexie.vxph.dht

import cn.hutool.core.util.IdUtil
import cn.hutool.crypto.digest.DigestUtil
import com.dampcake.bencode.Bencode
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.*
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.DatagramPacket
import io.netty.channel.socket.nio.NioDatagramChannel
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress


class DhtVerticle(val port: Int) : CoroutineVerticle() {

    private val log = LoggerFactory.getLogger("")

    private val group: EventLoopGroup = NioEventLoopGroup()
    private val bootstrap = Bootstrap()
    private var channel: Channel? = null

    override suspend fun start() {
        bootstrap.group(group)
            .channel(NioDatagramChannel::class.java)
            .option(ChannelOption.SO_BROADCAST, true)
            .handler(object : ChannelInitializer<Channel>() {
                override fun initChannel(ch: Channel) {
                    ch.pipeline().addLast(DhtChannelInboundHandlerAdapter())
                }
            })
        channel = bootstrap.bind(port).sync().channel()
        log.info("DHT 功能开始启动，监听端口：{}", port)
        send()
    }

    fun send() {
        val fastSimpleUUID = IdUtil.fastSimpleUUID()
        val mapOf = mapOf(
            "t" to fastSimpleUUID,
            "y" to "q",
            "q" to "find_node",
            "a" to mapOf("id" to bucket.nodeId, "target" to DigestUtil.sha1("zxc1"))
        )
        val encode = bencode.encode(mapOf)

        //router.bittorrent.com:6881
        //router.utorrent.com:6881
        //dht.transmissionbt.com:6881
        //dht.aelitis.com
        val inetSocketAddress = InetSocketAddress("router.bittorrent.com", 6881)
        val wrappedBuffer = Unpooled.wrappedBuffer(encode)
        val datagramPacket = DatagramPacket(wrappedBuffer, inetSocketAddress)
        log.info("send data , remote addrss:{}  becode :{} ", datagramPacket.recipient(), String(encode))
        channel!!.writeAndFlush(datagramPacket)
    }


    override suspend fun stop() {
        channel?.closeFuture()?.await()
        group.shutdownGracefully()
    }

}


fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(DhtVerticle(10086))
}
