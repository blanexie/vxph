package com.github.blanexie.vxph.user.entity

import com.fasterxml.jackson.databind.BeanDescription
import com.github.blanexie.vxph.common.BaseEntity
import jakarta.persistence.*

@Entity
data class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long?,
    @Column(unique = true, nullable = false)
    var name: String,
    @Column(unique = true, nullable = false)
    var code: String,
    @Column
    var description: String,

    @ManyToOne
    var parentRole: Role?,

    @ManyToMany
    var paths: List<Path>
) : BaseEntity()
