package com.github.blanexie.vxph.torrent.entity

import com.github.blanexie.vxph.common.entity.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class Label(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,
    var code: String,
    var name: String,
    var type: String,
) : BaseEntity()
