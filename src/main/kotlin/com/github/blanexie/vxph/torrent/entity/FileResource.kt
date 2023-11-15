package com.github.blanexie.vxph.torrent.entity

import com.github.blanexie.vxph.common.BaseEntity
import com.github.blanexie.vxph.user.entity.User
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

@Entity
data class FileResource(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long?,
    var hash: String,
    var name: String?,
    var suffix: String, //文件的后缀，同时也是表示文件的类型，
    var length: Long,
    @ManyToOne
    var owner: User,
) : BaseEntity()