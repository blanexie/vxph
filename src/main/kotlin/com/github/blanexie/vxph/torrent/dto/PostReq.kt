package com.github.blanexie.vxph.torrent.dto

import com.github.blanexie.vxph.torrent.entity.Post

data class PostReq(
    var id: Long?,
    var title: String,
    var coverImg: String?,
    var owner: Long?,
    var imgs: List<String>?,
    var markdown: String, //描述， 长文本
    var torrent: List<String>?,
    var status: Int //0:初始状态  1： 发布状态  2：封禁状态（无法announce和scrape） 3：下架
){

}