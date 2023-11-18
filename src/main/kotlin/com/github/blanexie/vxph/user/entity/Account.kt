package com.github.blanexie.vxph.user.entity

import com.github.blanexie.vxph.common.BaseEntity
import jakarta.persistence.*

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
    //拥有的可用邀请函
    var inviteCount: Int,
) : BaseEntity()