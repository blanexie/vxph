package com.github.blanexie.vxph.mail.entity

import com.github.blanexie.vxph.common.BaseEntity
import com.github.blanexie.vxph.user.entity.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne


@Entity
data class Email(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long?,
    var subject: String,
    @Column(name ="`from`")
    val from: String,
    @Column(name ="`to`")
    val to: String,
    val cc: String?,
    val text: String?,
    val html: String?,
    val file: String?,
    @ManyToOne
    val sender: User,
) : BaseEntity()
