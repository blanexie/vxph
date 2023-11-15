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
