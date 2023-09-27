package com.github.blanexie.vxph.dht

import cn.hutool.core.util.IdUtil
import cn.hutool.crypto.digest.DigestUtil
import com.dampcake.bencode.Bencode
import com.dampcake.bencode.Type
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.socket.DatagramPacket
import org.slf4j.LoggerFactory
import java.math.BigInteger


val bucket = KBucket(DigestUtil.sha1("vxph"))
val bencode = Bencode(true)
val processRequest = ProcessRequest(bucket)
val processResponse = ProcessResponse(bucket)

class DhtChannelInboundHandlerAdapter : ChannelInboundHandlerAdapter() {

    private val log = LoggerFactory.getLogger("")

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        log.info("receive pack,  channelRead start ")
        val buf = msg as DatagramPacket
        val sender = buf.sender()
        log.info("receive pack,   sender is  {}", sender)
        val dictionary = buf.bencodeContentDict()
        val y = dictionary.readString("y")
        if (y == "q") {
            val replyPack = processRequest.replyPack(dictionary)
            replyPack?.let {
                log.info("reply pack ,  becode str : {}", String(replyPack))
                val wrappedBuffer = Unpooled.wrappedBuffer(it)
                ctx.writeAndFlush(DatagramPacket(wrappedBuffer, sender))
            }
        }
        if (y == "r") {
            processResponse.replyPack(dictionary)
        }
    }
}

fun DatagramPacket.bencodeContentDict(): MutableMap<String, Any> {
    val content = this.content()
    val array = ByteArray(content.readableBytes())
    content.getBytes(content.readerIndex(), array)
    return bencode.decode(array, Type.DICTIONARY)
}
