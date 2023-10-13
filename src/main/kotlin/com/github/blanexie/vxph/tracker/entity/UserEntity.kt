package com.github.blanexie.vxph.tracker.entity

import java.time.LocalDateTime

class UserEntity {

    var id: Long? = null
    lateinit var name: String
    lateinit var email: String
    lateinit var password: String
    var sex: Int = 0
    var inviteId: Long = 0
    lateinit var createTime: LocalDateTime
    lateinit var updateTime: LocalDateTime
    var status: Int = 0

}