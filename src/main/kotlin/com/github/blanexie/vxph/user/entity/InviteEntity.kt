package com.github.blanexie.vxph.user.entity

import cn.hutool.core.bean.BeanUtil
import com.github.blanexie.vxph.core.sqlite.buildEntity
import com.github.blanexie.vxph.core.sqlite.find
import com.github.blanexie.vxph.core.sqlite.hikariDb
import com.github.blanexie.vxph.core.sqlite.setField
import com.github.blanexie.vxph.tracker.toEntity
import java.time.LocalDateTime

class InviteEntity {
    var code: String?=null         // 邀请码
    var sender: Long? = null         // 发邀请者
    lateinit var expire: LocalDateTime // 过期时间
    lateinit var email: String        // 接受邀请的邮箱

    lateinit var acceptTime: LocalDateTime //接收邀请的时间
    lateinit var createTime: LocalDateTime
    lateinit var updateTime: LocalDateTime
    var status: Int = 0            // 1: 表示已经接收   2：表示过期， 不会主动扫描过期的


    fun upsert() {
        val entity = BeanUtil.beanToMap(this).toEntity("Invite")
        hikariDb().upsert(entity, "code")
    }

    fun checkActive(): Boolean {
        //是否过期
        if (LocalDateTime.now().isAfter(expire)) {
            return false
        }
        //是否失效
        if (status != 0) {
            return false
        }
        return true
    }

    companion object {
        fun findByCode(code: String): InviteEntity? {
            val entity: List<InviteEntity> = buildEntity(InviteEntity::class)
                .setField(InviteEntity::code, code).find()
            return entity.firstOrNull()
        }
    }

}