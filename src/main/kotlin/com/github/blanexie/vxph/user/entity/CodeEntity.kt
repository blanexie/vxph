package com.github.blanexie.vxph.user.entity

import cn.hutool.db.Entity
import com.github.blanexie.vxph.core.sqlite.hikariDb
import java.time.LocalDateTime

class CodeEntity {
    var code: String? = null
    var content: String? = null
    var name: String? = null

    lateinit var createTime: LocalDateTime
    lateinit var updateTime: LocalDateTime
    var status: Int = 0

    companion object {

        fun findByCode(code: String): CodeEntity? {
            val table = Entity.create("Code").set("code", code)
            val codeEntitys = hikariDb().find(table, CodeEntity::class.java)
            return codeEntitys.firstOrNull()
        }
    }
}