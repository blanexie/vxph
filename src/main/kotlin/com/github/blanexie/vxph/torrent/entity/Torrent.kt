package com.github.blanexie.vxph.torrent.entity

import com.github.blanexie.vxph.common.BaseEntity
import com.github.blanexie.vxph.user.entity.User
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedBy
import java.time.LocalDateTime


@Entity
data class Torrent(
    @Id
    var infoHash: String,
    var title: String, //用户命名的
    var name: String, //torrent中的名称
    var length: Long,
    var comment: String,   // torrent中的注释
    var files: String,
    var creationDate: Long, //秒时间戳
    var createdBy: String,
    var pieceLength: Long,
    var singleFile: Boolean,

    //complete – 目前做种人数
    var complete: Int,
    //incomplete – 目前正在下载人数
    var incomplete: Int,
    //downloaded – 曾经下载完成过的人数
    var downloaded: Int,

    @OneToMany(fetch = FetchType.LAZY)
    var peer: List<Peer>,
    @ManyToOne(fetch = FetchType.LAZY)
    var owner: User,
    @ManyToOne(fetch = FetchType.LAZY)
    var post: Post,
) : BaseEntity()