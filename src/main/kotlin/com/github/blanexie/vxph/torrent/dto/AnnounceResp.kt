package com.github.blanexie.vxph.torrent.dto

import cn.hutool.core.util.StrUtil
import com.github.blanexie.vxph.common.bencode
import java.nio.ByteBuffer

class AnnounceResp(
    private val interval: Int,
    private var failReason: String?,
    private val peers: List<PeerResp>,
    private val peers6: List<PeerResp>,
) {


    /**
     * 将返回内容 使用bencode编码成字节数组
     */
    fun toBytes(compact: Int = 1): ByteArray {
        val resultMap = hashMapOf<String, Any>()
        if (StrUtil.isNotEmpty(failReason)) {
            //有异常， 返回错误信息
            resultMap["fail reason"] = failReason!!
            return bencode.encode(resultMap)
        }
        //请求间隔，单位是分钟
        resultMap["interval"] = interval
        //非紧凑格式响应
        if (compact == 0) {
            //非紧凑格式的，
            if (peers.isNotEmpty()) {
                resultMap["peers"] = peers
            }
            if (peers6.isNotEmpty()) {
                resultMap["peers6"] = peers6
            }
            return bencode.encode(resultMap)
        }

        //紧凑格式响应
        if (peers.isNotEmpty()) {
            val allocate = ByteBuffer.allocate(peers.size * 6)
            peers.forEach {
                allocate.put(it.toBytes())
            }
            resultMap["peers"] = allocate.array()
        }
        if (peers6.isNotEmpty()) {
            val allocate = ByteBuffer.allocate(peers.size * 18)
            peers6.forEach {
                allocate.put(it.toBytes())
            }
            resultMap["peers6"] = allocate.array()
        }
        return bencode.encode(resultMap)
    }

}
