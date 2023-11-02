package com.github.blanexie.vxph.user.entity

import cn.hutool.db.Entity
import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.blanexie.vxph.core.sqlite.hikariDb
import java.time.LocalDateTime

class UserEntity {

    var id: Long? = null
    lateinit var name: String
    lateinit var email: String
    @JsonIgnore
    lateinit var password: String
    var sex: Int = 0
    var inviteId: Long = 0
    lateinit var createTime: LocalDateTime
    lateinit var updateTime: LocalDateTime
    var status: Int = 0

    companion object {
        fun findById(id: Long): UserEntity? {
            val table = Entity.create("User").set("id", id)
            val userEntity = hikariDb().find(table, UserEntity::class.java)
            return userEntity.firstOrNull()
        }

        fun findByName(name: String): UserEntity? {
            val table = Entity.create("User").set("name", name)
            val userEntity = hikariDb().find(table, UserEntity::class.java)
            return userEntity.firstOrNull()
        }
    }
}