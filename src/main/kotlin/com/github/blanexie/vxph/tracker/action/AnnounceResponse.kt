package com.github.blanexie.vxph.tracker.action

import com.github.blanexie.vxph.dht.bencode
import com.github.blanexie.vxph.tracker.*
import com.github.blanexie.vxph.tracker.entity.PeerEntity
import com.github.blanexie.vxph.utils.objectMapper
import io.vertx.core.buffer.Buffer
import java.nio.ByteBuffer
import java.time.Duration
import java.time.LocalDateTime


/**
 *    //warnging message, 当发生非致命性错误时, Tracker 返回的可读的警告信息
 *         //interval, 对应的 Value 是终端在下一次请求 BT Tracker 前应等待的时间 (以秒为单位)
 *         //min interval, 对应的 Value 是终端在下一次请求 BT Tracker 前的最短等待时间 (以秒为单位)
 *         //complete, 对应的 Value 表明当前已完成整个资源下载的 peer 的数量
 *         //incomplete, 对应的 Value 表明当前未完成整个资源下载的 peer 的数量
 *         //peers, 对应的 Value 是一个字典的列表, 即列表的每一个元素都是一个字典, 每个字典包含有两个 Key, 分别为:
 *         //
 *         //peer id, peer 结点的 Id
 *         //IP, peer 结点的 IP 地址
 *         //Port, peer 结点的端口
 */
class AnnounceResponse(
    val interval: Int,
    val minInterval: Int,
    val complete: Int,
    val inComplete: Int,
    val peers: List<PeerIpAddr>,
    val peers6: List<PeerIpAddr>
) {

    fun toBencodeByte(compact: Int = 1): Buffer {
        if (compact == 1) {
            val peersByteArray = ByteBuffer.allocate(peers.size * 6)
            peers.forEach {
                peersByteArray.put(it.ip).put((it.port and 0xFF).toByte()).put((it.port ushr 8 and 0xFF).toByte())
            }
            val peers6ByteArray = ByteBuffer.allocate(peers.size * 18)
            peers6.forEach {
                peers6ByteArray.put(it.ip).put((it.port and 0xFF).toByte()).put((it.port ushr 8 and 0xFF).toByte())
            }
            val map = mapOf(
                "interval" to interval,
                "complete" to complete,
                "min_interval" to minInterval,
                "in_complete" to inComplete,
                "peers" to peersByteArray.array(),
                "peers6" to peers6ByteArray.array()
            )
            return Buffer.buffer(bencode.encode(map))
        } else {
            val map = mapOf(
                "interval" to interval,
                "complete" to complete,
                "min_interval" to minInterval,
                "in_complete" to inComplete,
                "peers" to this.peers,
                "peers6" to this.peers6
            )
            return Buffer.buffer(bencode.encode(map))
        }
    }

    companion object {
        //构建返回对象
        fun build(peerList: List<PeerEntity>): AnnounceResponse {
            val now = LocalDateTime.now()
            val peerEntities = peerList.sortedBy(PeerEntity::left)
                .filter { Duration.between(it.updateTime, now).toMinutes() < peerExpireMinutes }
                .filter { it.event == EVENT_START || it.event == EVENT_COMPLETE || it.event == EVENT_EMPTY }.stream()
                .limit(75).toList()

            val peers = arrayListOf<PeerIpAddr>()
            val peers6 = arrayListOf<PeerIpAddr>()
            var complete = 0
            var inComplete = 0
            peerEntities.forEach {
                if (it.left == 0L) {
                    complete++
                } else {
                    inComplete++
                }
                val socketAddress = objectMapper.readValue(it.remoteAddress, Map::class.java)
                val ipv4 = socketAddress["ipv4"]
                val ipv6 = socketAddress["ipv6"]
                val port = socketAddress["port"]
                ipv4?.let { ip ->
                    peers.add(PeerIpAddr(it.peerId, ip as ByteArray, port as Int))
                }
                ipv6?.let { ip ->
                    peers6.add(PeerIpAddr(it.peerId, ip as ByteArray, port as Int))
                }
            }

            return AnnounceResponse(
                peerAnnounceIntervalMinutes, peerAnnounceIntervalMinutes, complete, inComplete, peers, peers6
            )
        }
    }

}

class PeerIpAddr(val peerId: String, val ip: ByteArray, val port: Int)