package com.github.blanexie.vxph.dht

import cn.hutool.core.util.RandomUtil
import cn.hutool.crypto.digest.DigestUtil
import com.github.blanexie.vxph.dht.message.FindNodeRequest
import com.github.blanexie.vxph.dht.message.PingRequest
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
import java.net.InetSocketAddress


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
        sendFindNode()
    }

    fun sendFindNode() {
        //router.bittorrent.com:6881
        //router.utorrent.com:6881
        //dht.transmissionbt.com:6881
        //dht.aelitis.com
        val inetSocketAddress = InetSocketAddress("192.168.1.6", 16881)
        val nodeId = NodeId(DigestUtil.sha1(RandomUtil.randomString(9)))
        val target = NodeId(DigestUtil.sha1(RandomUtil.randomString(9)))
        val node = Node(nodeId, System.currentTimeMillis(), inetSocketAddress)
        val findNodeRequest = FindNodeRequest(node, target, kBucket)
        findNodeRequest.send(channel!!, kBucket)
    }


    override suspend fun stop() {
        channel?.closeFuture()?.await()
        channel?.close()?.sync()
        group.shutdownGracefully()
    }

}


fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(DhtVerticle(17616))
}
