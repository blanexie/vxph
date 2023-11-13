package com.github.blanexie.vxph.torrent.dto

import cn.hutool.core.bean.BeanUtil
import cn.hutool.core.util.StrUtil

class AnnounceResp(
    val interval: Int,
    var failReason: String?,
    val peers: List<PeerResp>,
    val peers6: List<PeerResp>,
) {

    fun toBencodeByte(): Byte {
        if (StrUtil.isEmpty(failReason)) {
            val mapOf = hashMapOf<String, Any>("interval" to interval)
            if (peers.isNotEmpty()) {
                mapOf["peers"] = peers
            }
            if (peers6.isNotEmpty()) {
                mapOf["peers6"] = peers6
            }

        }
        if (StrUtil.isNotEmpty(failReason)) {
            val mapOf = hashMapOf<String, Any>("interval" to interval)
            mapOf["fail reason"] = failReason!!


        }
    }

}

class PeerResp(val peerId: String, val ip: String, val port: Int)