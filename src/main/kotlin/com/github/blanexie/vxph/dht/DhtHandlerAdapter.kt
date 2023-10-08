package com.github.blanexie.vxph.dht

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.DatagramPacket
import org.slf4j.LoggerFactory


class DhtHandlerAdapter : SimpleChannelInboundHandler<DatagramPacket>() {

  private val log = LoggerFactory.getLogger("")
  override fun channelRead0(ctx: ChannelHandlerContext, msg: DatagramPacket) {
    log.info("receive pack,  channelRead start ")
    val sender = msg.sender()
    log.info("receive pack,   sender is  {}", sender)
    val dictionary = msg.bencodeContentDict()
    val y = dictionary.readString("y")
    if (y == "q") {
      val baseRequest = requestFactory.process(dictionary, sender)
      baseRequest?.let {
        val baseResponse = it.apply(kBucket)
        baseResponse.send(ctx.channel(), kBucket)
      }
    }
    if (y == "r") {
      log.info("receive response ,  becode str : {}", dictionary)
      val baseResponse = responseFactory.process(dictionary, sender)
      baseResponse?.apply(kBucket)
    }
  }
}
