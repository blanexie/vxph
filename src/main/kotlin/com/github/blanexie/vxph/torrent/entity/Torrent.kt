package com.github.blanexie.vxph.torrent.entity

import com.github.blanexie.vxph.common.entity.BaseEntity
import com.github.blanexie.vxph.user.entity.User
import jakarta.persistence.*


@Entity
data class Torrent(
    @Id
    var infoHash: String,
    var title: String, //用户命名的
    var name: String, //torrent中的名称
    @Column(name="`length`")
    var length: Long,
    @Column(name="`comment`")
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
    @Column(name="`peer`")
    var peer: List<Peer>,
    var owner: Long,
    @ManyToOne(fetch = FetchType.LAZY)
    var post: Post,
) : BaseEntity()