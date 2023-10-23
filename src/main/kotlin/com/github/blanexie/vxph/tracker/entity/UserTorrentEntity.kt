package com.github.blanexie.vxph.tracker.entity

import cn.hutool.core.bean.BeanUtil
import cn.hutool.db.Db
import cn.hutool.db.Entity
import com.github.blanexie.vxph.tracker.toEntity
import com.github.blanexie.vxph.core.hikariDataSource
import java.time.LocalDateTime

class UserTorrentEntity() {

    lateinit var passKey: String
    lateinit var infoHash: String
    var userId: Long = 0
    var peerId: String? = null

    lateinit var createTime: LocalDateTime
    lateinit var updateTime: LocalDateTime
    var status: Int = 0

    fun upsert() {
        val entity = BeanUtil.beanToMap(this).toEntity("UserTorrent")
        Db.use(hikariDataSource())
            .upsert(entity, "passkey", "infoHash")
    }


    fun updatePeerId(peerId: String?) {
        this.peerId = peerId
        this.updateTime = LocalDateTime.now()
        val setEntity = Entity.create("UserTorrent").set("peerId", this.peerId)
            .set("updateTime", updateTime)
        val whereEntity = Entity.create("UserTorrent").set("passKey", this.passKey)
            .set("infoHash", infoHash)
        Db.use(hikariDataSource()).update(setEntity, whereEntity)
    }


    companion object {
        fun findByPasskey(passKey: String, infoHash: String): UserTorrentEntity? {
            val entity = Entity.create("UserTorrent").set("passkey", passKey)
                .set("infoHash", infoHash)
            val list = Db.use(hikariDataSource())
                .find(entity, UserTorrentEntity::class.java)

            return list.firstOrNull()
        }

    }

}