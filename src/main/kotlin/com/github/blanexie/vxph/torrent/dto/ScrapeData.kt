package com.github.blanexie.vxph.torrent.dto
data class ScrapeData(
    val complete: Int, // – 目前做种人数
    val incomplete: Int, // – 目前正在下载人数
    val downloaded: Int, // – 曾经下载完成过的人数)
)