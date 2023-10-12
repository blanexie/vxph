package com.github.blanexie.vxph.tracker.action

import cn.hutool.core.util.StrUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.blanexie.vxph.tracker.entity.PeerEntity
import com.github.blanexie.vxph.tracker.objectMapper


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
    companion object {

        fun build(peerEntities: List<PeerEntity>) {
            val peers = arrayListOf<PeerIpAddr>()
            val peers6 = arrayListOf<PeerIpAddr>()
            peerEntities.forEach {
                val socketAddress = objectMapper.readValue(it.remoteAddress, Map::class.java)
                val ipv4 = socketAddress["ipv4"]
                val ipv6 = socketAddress["ipv6"]
                val port = socketAddress["port"]
                ipv4?.let {

                }


            }


        }

    }

}

class PeerIpAddr(val peerId: String, val ipv4: ByteArray, val port: Short)