package com.github.blanexie.vxph.tracker.entity

import cn.hutool.core.bean.BeanUtil
import cn.hutool.db.DbUtil
import com.github.blanexie.vxph.tracker.hikariDataSource
import com.github.blanexie.vxph.tracker.toEntity
import java.time.LocalDateTime
import kotlin.properties.Delegates

class TorrentEntity {

    lateinit var infoHash: String

    lateinit var name: String
    var length: Long = 0

    var comment: String? = null
    lateinit var files: String      //json格式， 单文件也是json格式
    var creationDate: Long? = null  //建立的时间，是从1970年1月1日00:00:00到现在的秒数
    var pieceLength: Long = 0
    lateinit var info: String
    lateinit var publisher: String
    lateinit var publisherUrl: String

    var singleFile: Int = 1         //1 单文件。 2 多文件

    lateinit var nodes: String      //hex编码

    lateinit var createTime: LocalDateTime
    lateinit var updateTime: LocalDateTime
    var status: Int = 0

    fun upsert() {
        val entity = BeanUtil.beanToMap(this).toEntity("torrent")
        DbUtil.use(hikariDataSource()).upsert(entity, "infoHash")
    }


}