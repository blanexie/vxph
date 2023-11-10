package com.github.blanexie.vxph.user.entity

import com.github.blanexie.vxph.common.BaseEntity
import jakarta.persistence.*


@Entity
data class Path(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long?,
    var name: String,
    var path: String,
    var method: String,
    @ManyToMany
    var roles: List<Role>
) : BaseEntity()