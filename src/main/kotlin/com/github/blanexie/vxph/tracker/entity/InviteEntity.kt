package com.github.blanexie.vxph.tracker.entity

import java.time.LocalDateTime

class InviteEntity {

    var id: Long? = null
    var sender: Long? = null         // 发邀请者
    lateinit var code: String         // 邀请码
    lateinit var email: String        // 接受邀请的邮箱
    lateinit var expire: LocalDateTime // 过期时间

    lateinit var acceptTime: LocalDateTime //接收邀请的时间

    lateinit var createTime: LocalDateTime
    lateinit var updateTime: LocalDateTime
    var status: Int = 0            // 1: 表示已经接收   2：表示过期， 不会主动扫描过期的
}