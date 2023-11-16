package com.github.blanexie.vxph.torrent.entity

import com.github.blanexie.vxph.common.BaseEntity
import com.github.blanexie.vxph.user.entity.User
import jakarta.persistence.*

@Entity
data class Post(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long?,
    var title: String,
    @OneToOne(fetch = FetchType.LAZY)
    var coverImg: FileResource?,
    @ManyToOne(fetch = FetchType.LAZY)
    var owner: User,
    @OneToMany(fetch = FetchType.LAZY)
    var imgs: List<FileResource>,

    var markdown: String, //描述， 长文本
    @OneToMany(fetch = FetchType.LAZY)
    var torrent: List<Torrent>,
) : BaseEntity()