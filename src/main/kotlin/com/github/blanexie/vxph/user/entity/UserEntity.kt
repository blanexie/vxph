package com.github.blanexie.vxph.user.entity

import cn.hutool.core.bean.BeanUtil
import cn.hutool.db.Entity
import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.blanexie.vxph.core.sqlite.buildEntity
import com.github.blanexie.vxph.core.sqlite.find
import com.github.blanexie.vxph.core.sqlite.hikariDb
import com.github.blanexie.vxph.core.sqlite.setField
import com.github.blanexie.vxph.tracker.toEntity
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


    fun upsert() {
        val entity = BeanUtil.beanToMap(this).toEntity("User")
        hikariDb().upsert(entity, "id")
    }

    companion object {
        fun findById(id: Long): UserEntity? {
            val table = Entity.create("User").set("id", id)
            val userEntity = hikariDb().find(table, UserEntity::class.java)
            return userEntity.firstOrNull()
        }

        fun findByEmail(email: String): UserEntity? {
            val userEntity: List<UserEntity> = buildEntity(UserEntity::class).setField(UserEntity::email, email)
                .find()
            return userEntity.firstOrNull()
        }

        fun findByName(name: String): UserEntity? {
            val table = Entity.create("User").set("name", name)
            val userEntity = hikariDb().find(table, UserEntity::class.java)
            return userEntity.firstOrNull()
        }
    }
}