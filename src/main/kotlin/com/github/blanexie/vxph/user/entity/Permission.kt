package com.github.blanexie.vxph.user.entity

import com.github.blanexie.vxph.common.BaseEntity
import com.github.blanexie.vxph.user.dto.PermissionType
import jakarta.persistence.*


@Entity
data class Permission(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long?,
    var name: String,
    @Column(unique = true)
    var code: String,

    @Enumerated(EnumType.STRING)
    var type: PermissionType,

    @ManyToMany
    var roles: List<Role>
) : BaseEntity()