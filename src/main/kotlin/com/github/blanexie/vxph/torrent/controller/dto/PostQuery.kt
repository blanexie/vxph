package com.github.blanexie.vxph.torrent.controller.dto

data class PostQuery(
    val keyword: String?,
    val page: Int,
    val pageSize: Int,
) {}