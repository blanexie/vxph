package com.github.blanexie.vxph.user.entity

import com.github.blanexie.vxph.common.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class Invite(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long?,
    @Column(unique = true)
    var code: String,
    var receiveEmail: String,
    @ManyToOne
    var sender: User,

    var acceptTime: LocalDateTime?
) : BaseEntity()