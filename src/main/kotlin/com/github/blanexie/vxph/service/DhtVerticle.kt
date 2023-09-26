package com.github.blanexie.vxph.service

import cn.hutool.core.util.IdUtil
import cn.hutool.crypto.digest.DigestUtil
import com.dampcake.bencode.Bencode
import com.dampcake.bencode.Type
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBufUtil
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioDatagramChannel
import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.AbstractCoroutine
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.math.BigInteger
import java.nio.ByteBuffer
import java.util.*
import kotlin.experimental.xor


val bencode = Bencode(true)


private val log = LoggerFactory.getLogger("")

val nodeId = DigestUtil.sha1("vxph")

val before = PriorityQueue<Node>(8) { o1, o2 ->
    o1.difference.divide(o2.difference).signum()
}

val after = PriorityQueue<Node>(8) { o1, o2 ->
    o1.difference.divide(o2.difference).signum()
}

val port = 10086

fun main() {
    start()

}


fun start() {
    val group: EventLoopGroup = NioEventLoopGroup()
    try {
        val bootstrap = Bootstrap()
        bootstrap.group(group)
            .channel(NioDatagramChannel::class.java)
            .option(ChannelOption.SO_BROADCAST, true)
            .handler(object : ChannelInitializer<Channel>() {
                override fun initChannel(ch: Channel) {
                    ch.pipeline().addLast(object : ChannelInboundHandlerAdapter() {
                        override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
                            val buf = msg as io.netty.channel.socket.DatagramPacket
                            val sender = buf.sender()
                            log.info("sender {}", sender)
                            val content = buf.content()
                            val array = ByteArray(content.readableBytes())
                            content.getBytes(content.readerIndex(), array)
                            val dictionary = bencode.decode(array, Type.DICTIONARY)
                            val y = readString(dictionary, "y")
                            val t = readString(dictionary, "t")
                            if (y == "q") {
                                val q = readString(dictionary, "q")
                                when (q) {
                                    "ping" -> processPing(dictionary)
                                    "find_node" -> processFindNode(dictionary)
                                    "get_peers" -> processGetPeers(dictionary)
                                    "announce_peer" -> processAnnouncePeer(dictionary)
                                }
                            }
                        }
                    })
                }
            })
        val channel: Channel = bootstrap.bind(port).sync().channel()
        channel.closeFuture().await()
    } finally {
        group.shutdownGracefully()
    }
}

private fun readString(dict: Map<String, Any>, key: String): String {
    val any = dict[key] as ByteBuffer
    return String(any.array())
}

private fun processFindNode(dict: Map<String, Any>): ByteArray {
    val a = dict["a"] as Map<*, *>
    val target = a["target"] as ByteBuffer
    val difference = difference(nodeId, target.array())
    val mutableMapOf = mutableMapOf<String, Any>()
    mutableMapOf["id"] = nodeId
    if (difference.signum() > 0) {
        val toList = after.map { it.id }.toList()
        mutableMapOf["nodes"] = toList
    } else {
        val toList = before.map { it.id }.toList()
        mutableMapOf["nodes"] = toList
    }

    val id = a["id"] as ByteBuffer
    add(id.array())

    val resp = mapOf("t" to dict["t"], "y" to "r", "r" to mutableMapOf)
    return bencode.encode(resp)
}

private fun processPing(dict: Map<String, Any>): ByteArray {
    val resp = mapOf("t" to dict["y"], "y" to "r", "r" to mapOf("id" to nodeId))
    return bencode.encode(resp)
}

private fun processGetPeers(dict: Map<String, Any>) {
    val r = dict["r"] as Map<*, *>
    val id = r["id"] as ByteArray
    add(id)

    val nodes = r["nodes"] as ByteArray
    var index = 0
    while (index < nodes.size) {
        val range = nodes.copyOfRange(index, index + 20)
        add(range)
        index += 20
    }

}

private fun processAnnouncePeer(dict: Map<String, Any>) {


}


private fun difference(id: ByteArray, id2: ByteArray): BigInteger {
    val byteArray = ByteArray(20)
    for (i in 0..19) {
        val byte = id[i]
        val byte2 = id2[i]
        val b = byte xor byte2
        byteArray[i] = b
    }
    return BigInteger(byteArray)
}


private fun add(id: ByteArray) {
    val difference1 = difference(nodeId, id)
    if (difference1.signum() > 0) {
        after.offer(Node(id, System.currentTimeMillis(), difference1))
    } else {
        before.offer(Node(id, System.currentTimeMillis(), difference1))
    }
}


data class Node(
    val id: ByteArray,
    val lastChange: Long, // 最新的活跃时间
    val difference: BigInteger, //距离
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Node

        if (!id.contentEquals(other.id)) return false
        if (lastChange != other.lastChange) return false
        if (difference != other.difference) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.contentHashCode()
        result = 31 * result + lastChange.hashCode()
        result = 31 * result + difference.hashCode()
        return result
    }

}




