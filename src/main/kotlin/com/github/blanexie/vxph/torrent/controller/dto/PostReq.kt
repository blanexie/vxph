package com.github.blanexie.vxph.torrent.controller.dto

import com.github.blanexie.vxph.torrent.entity.FileResource
import com.github.blanexie.vxph.torrent.entity.Label

data class PostReq(
    var id: Long?,
    var title: String,
    var coverImg: FileResource ,
    var imgs: List<FileResource> ,
    var markdown: String, //描述， 长文本
    var type:String,
    var labels:List<Label>,
    var status: Int //0:初始状态  1： 发布状态  2：封禁状态（无法announce和scrape） 3：下架
){

}