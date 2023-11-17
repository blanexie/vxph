package com.github.blanexie.vxph.user.entity

import com.github.blanexie.vxph.common.BaseEntity
import jakarta.persistence.*

@Entity
data class Invite(
    @Id
    var code: String?,
    @Column(unique = true, nullable = false)
    var receiveEmail: String,
    @ManyToOne
    var sender: User,

) : BaseEntity()