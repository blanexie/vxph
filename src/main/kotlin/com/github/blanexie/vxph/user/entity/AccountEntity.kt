package com.github.blanexie.vxph.user.entity

import cn.hutool.db.Entity
import com.github.blanexie.vxph.core.sqlite.buildEntity
import com.github.blanexie.vxph.core.sqlite.find
import com.github.blanexie.vxph.core.sqlite.hikariDb
import com.github.blanexie.vxph.core.sqlite.set
import java.time.LocalDateTime


/**
 * 账户信息， 保存用户的数据
 */
class AccountEntity {
    var id: Long? = null
    var userId: Long? = null
    lateinit var role: String

    var downloaded: Long = 0
    var uploaded: Long = 0
    var integral: Int = 0
    lateinit var level: String

    var inviteCount: Int = 0  //邀请函数量


    lateinit var createTime: LocalDateTime
    lateinit var updateTime: LocalDateTime
    var status: Int = 0

    companion object {
        fun findById(id: Long): AccountEntity? {
            val accountEntities: List<AccountEntity> = buildEntity(AccountEntity::class)
                .set(AccountEntity::id, id).find()
            return accountEntities.firstOrNull()
        }

        fun findByUserId(userId: Long): AccountEntity? {
            val accountEntities: List<AccountEntity> = buildEntity(AccountEntity::class)
                .set(AccountEntity::userId, userId)
                .find()
            return accountEntities.firstOrNull()
        }
    }
}
