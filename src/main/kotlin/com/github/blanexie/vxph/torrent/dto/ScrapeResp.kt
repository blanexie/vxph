package com.github.blanexie.vxph.torrent.dto

import cn.hutool.core.bean.BeanUtil
import com.github.blanexie.vxph.common.bencode

data class ScrapeResp(
    val files: Map<String, ScrapeData>
) {

    fun toBytes(): ByteArray {
        val beanToMap = BeanUtil.beanToMap(this)
        return bencode.encode(beanToMap)
    }
}

data class ScrapeData(
    val complete: Int, // – 目前做种人数
    val incomplete: Int, // – 目前正在下载人数
    val downloaded: Int, // – 曾经下载完成过的人数)
)