package com.github.blanexie.vxph.user.entity

import com.github.blanexie.vxph.common.entity.BaseEntity
import jakarta.persistence.*

@Entity
data class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,
    @Column(unique = true)
    var name: String,
    @Column(unique = true)
    var code: String,
    @Column
    var description: String,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable
    var permissions: List<Permission>

) : BaseEntity()
