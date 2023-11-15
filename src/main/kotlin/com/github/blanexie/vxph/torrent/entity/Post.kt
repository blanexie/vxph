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
    @OneToOne
    var coverImg: FileResource?,
    @OneToOne
    var owner: User,
    @OneToMany
    var imgs: List<FileResource>,

    var markdown: String, //描述， 长文本
    @OneToMany
    var torrent: List<Torrent>,
):BaseEntity()