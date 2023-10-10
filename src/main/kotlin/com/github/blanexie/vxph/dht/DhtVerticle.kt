package com.github.blanexie.vxph.dht

import cn.hutool.core.util.IdUtil
import cn.hutool.crypto.digest.DigestUtil
import com.github.blanexie.vxph.dht.message.FindNodeRequest
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.DatagramChannel
import io.netty.channel.socket.nio.NioDatagramChannel
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress


class DhtVerticle(val port: Int) : CoroutineVerticle() {

    private val log = LoggerFactory.getLogger(this::class.java)

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

        vertx.setPeriodic(nodeRefreshTime) {
            log.info("DHT 开始刷新KBucket中的Node")
            refreshDhtNode()
        }

    }

    //初始化K桶，让系统加入到DHT网络中
    private fun refreshDhtNode() {
       val findNodeRequests = kBucket.regularity()
        findNodeRequests.forEach {
            it.send(channel!!, kBucket)
        }
        val pingRequests = kBucket.findUnUsedNode()
        pingRequests.forEach { p ->
            p.send(channel!!, kBucket)
        }
    }


    override suspend fun stop() {
        channel?.closeFuture()?.await()
        channel?.close()?.sync()
        group.shutdownGracefully()
    }

}


fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(DhtVerticle(17626))
}
