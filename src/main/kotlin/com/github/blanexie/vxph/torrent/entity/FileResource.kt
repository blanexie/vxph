package com.github.blanexie.vxph.torrent.entity

import com.github.blanexie.vxph.common.entity.BaseEntity
import com.github.blanexie.vxph.user.entity.User
import jakarta.persistence.*

@Entity
data class FileResource(
    @Id
    var hash: String,
    var name: String?,  //传给后端的文件名称
    var suffix: String, //文件的后缀，同时也是表示文件的类型，
    var length: Long,
    var owner: Long,
) : BaseEntity()
