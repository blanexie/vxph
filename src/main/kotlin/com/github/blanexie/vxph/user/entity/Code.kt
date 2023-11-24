package com.github.blanexie.vxph.user.entity

import com.github.blanexie.vxph.common.entity.BaseEntity
import jakarta.persistence.*

@Entity
data class Code(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long?,
    @Column(unique = true, nullable = false)
    var name: String,
    @Column(unique = true, nullable = false)
    var code: String,

    @Column(nullable = false)
    var value: String,
    @Column(nullable = false)
    var parentId: Long = 0L,
) : BaseEntity()