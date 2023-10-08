package com.github.blanexie.vxph.dht

import io.netty.bootstrap.Bootstrap
import io.netty.buffer.*
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.DatagramChannel
import io.netty.channel.socket.DatagramPacket
import io.netty.channel.socket.nio.NioDatagramChannel
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.slf4j.LoggerFactory


class DhtVerticle(val port: Int) : CoroutineVerticle() {

    private val log = LoggerFactory.getLogger("")

    private val group: EventLoopGroup = NioEventLoopGroup()
    private val bootstrap = Bootstrap()
    private var channel: Channel? = null

    override suspend fun start() {
        bootstrap.group(group)
            .channel(NioDatagramChannel::class.java)
            .handler(object : ChannelInitializer<DatagramChannel>() {
                override fun initChannel(ch: DatagramChannel) {
                    ch.pipeline().addLast(DhtHandlerAdapter())
                }
            })
        channel = bootstrap.bind(port).sync().channel()
        log.info("DHT 功能开始启动，监听端口：{}", port)


        vertx.setPeriodic(10 * 60 * 1000) {

        }
    }

    fun sendFindNode(node: Node) {
        val t = System.currentTimeMillis().toString()
        val mapOf = mapOf(
            "t" to t,
            "y" to "q",
            "q" to "find_node",
            "a" to mapOf("id" to kBucket.nodeId, "target" to node.nodeId)
        )
        val encode = bencode.encode(mapOf)

        //router.bittorrent.com:6881
        //router.utorrent.com:6881
        //dht.transmissionbt.com:6881
        //dht.aelitis.com
        val inetSocketAddress = node.ip4
        val wrappedBuffer = Unpooled.wrappedBuffer(encode)
        val datagramPacket = DatagramPacket(wrappedBuffer, inetSocketAddress)
        log.info("send data , remote addrss:{}  becode :{} ", datagramPacket.recipient(), String(encode))
        channel!!.writeAndFlush(datagramPacket).sync()
    }


    override suspend fun stop() {
        channel?.closeFuture()?.await()
        channel?.close()?.sync()
        group.shutdownGracefully()
    }

}


fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(DhtVerticle(10086))
}
