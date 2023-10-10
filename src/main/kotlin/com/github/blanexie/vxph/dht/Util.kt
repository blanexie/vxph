

package com.github.blanexie.vxph.dht

import cn.hutool.core.util.RandomUtil
import cn.hutool.crypto.digest.DigestUtil
import com.dampcake.bencode.Bencode
import com.dampcake.bencode.Type
import com.github.blanexie.vxph.dht.message.RequestFactory
import com.github.blanexie.vxph.dht.message.ResponseFactory
import io.netty.channel.socket.DatagramPacket
import io.netty.util.NetUtil
import java.net.InetSocketAddress
import java.nio.ByteBuffer

val kBucket = KBucket(NodeId(DigestUtil.sha1(RandomUtil.randomString(12))))
val bencode = Bencode(true)
val requestFactory = RequestFactory()
val responseFactory = ResponseFactory(kBucket)

//10分钟刷新一次
const val nodeRefreshTime = 30 * 1000L
const val nodeUnUsedTime = nodeRefreshTime * 2   //过期时间是刷新时间的2倍
const val bucketSize = 8

//router.bittorrent.com:6881
//router.utorrent.com:6881
//dht.transmissionbt.com:6881
//dht.aelitis.com
val initNodeInetSocketAddress = InetSocketAddress("192.168.1.6", 16881)

fun Map<String, Any>.readString(key: String): String {
    val any = this[key] as ByteBuffer
    return String(any.array())
}

fun Map<String, Any>.readByteArray(key: String): ByteArray {
    val any = this[key] as ByteBuffer
    return any.array()
}

fun Map<String, Any>.readLongIfExist(key: String): Long? {
    val any = this[key]
    return any?.let { any as Long }
}

fun Map<String, Any>.readMap(key: String): Map<String, Any> {
    @Suppress("UNCHECKED_CAST")
    return this[key] as Map<String, Any>
}

fun ByteArray.readNodeId(index: Int = 0): NodeId {
    val key = this.copyOfRange(index, index + 20)
    return NodeId(key)
}

fun ByteArray.readIp4(index: Int = 0): InetSocketAddress {
    val ipBytes = this.copyOfRange(index, index + 4)
    val ipAddress = NetUtil.bytesToIpAddress(ipBytes)
    val portBytes = this.copyOfRange(index + 4, index + 6)
    val port = portBytes[1].toInt() and 0xFF or
            (portBytes[0].toInt() and 0xFF shl 8)
    return InetSocketAddress(ipAddress, port)
}

fun ByteArray.readIp6(index: Int = 0): InetSocketAddress {
    val ipBytes = this.copyOfRange(index, index + 16)
    val ipAddress = NetUtil.bytesToIpAddress(ipBytes)
    val portBytes = this.copyOfRange(index + 16, index + 18)
    val port = portBytes[1].toInt() and 0xFF or
            (portBytes[0].toInt() and 0xFF shl 8)
    return InetSocketAddress(ipAddress, port)
}

fun DatagramPacket.bencodeContentDict(): MutableMap<String, Any> {
    val content = this.content()
    val array = ByteArray(content.readableBytes())
    content.getBytes(content.readerIndex(), array)
    return bencode.decode(array, Type.DICTIONARY)
}
