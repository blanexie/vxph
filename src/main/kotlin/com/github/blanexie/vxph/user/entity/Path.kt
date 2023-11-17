package com.github.blanexie.vxph.user.entity

import com.github.blanexie.vxph.common.BaseEntity
import jakarta.persistence.*


@Entity
data class Path(
    @Id
    var path: String?,
    var name: String,
    var method: String,
    @ManyToMany
    var roles: List<Role>
) : BaseEntity()