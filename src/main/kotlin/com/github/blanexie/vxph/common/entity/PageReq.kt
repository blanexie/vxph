package com.github.blanexie.vxph.common.entity

data class PageReq(
    val page: Int,
    val pageSize: Int,
    val searchKey: String? = ""
) {
}