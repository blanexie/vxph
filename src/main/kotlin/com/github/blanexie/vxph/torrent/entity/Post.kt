package com.github.blanexie.vxph.torrent.entity

import com.github.blanexie.vxph.common.entity.BaseEntity
import com.github.blanexie.vxph.user.entity.User
import jakarta.persistence.*
import kotlin.jvm.Transient

@Entity
data class Post(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long?,
    var title: String,
    @ManyToOne(fetch = FetchType.LAZY)
    var coverImg: FileResource?,
    var owner: Long,
    @ManyToMany(fetch = FetchType.LAZY)
    var imgs: List<FileResource>,

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post")
    var torrents:List<Torrent>,

    @ManyToMany(fetch = FetchType.LAZY)
    var labels: List<Label>,  //标签
    var markdown: String, //描述， 长文本
) : BaseEntity() {


    fun publish() {
        this.status = 1
    }

}