package com.github.blanexie.vxph.user.entity

import com.github.blanexie.vxph.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToOne

@Entity
data class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long?,
    @Column
    var upload: Long,
    @Column
    var download: Long,

    @Column
    var uploadReal: Long,
    @Column
    var downloadReal: Long,
    @Column
    var score: Long,

    @Column
    var level: String,

    @OneToOne
    var user: User,
) : BaseEntity()