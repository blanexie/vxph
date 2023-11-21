package com.github.blanexie.vxph.user.entity

import com.github.blanexie.vxph.common.BaseEntity
import com.github.blanexie.vxph.user.dto.PermissionType
import jakarta.persistence.*


@Entity
data class Permission(
    @Id
    var code: String,
    var name: String,
    var description: String,
    @Enumerated(EnumType.STRING)
    var type: PermissionType,
) : BaseEntity()