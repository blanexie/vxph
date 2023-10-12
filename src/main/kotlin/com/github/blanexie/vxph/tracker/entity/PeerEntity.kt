package com.github.blanexie.vxph.tracker.entity

import cn.hutool.core.bean.BeanUtil
import cn.hutool.db.Db
import cn.hutool.db.Entity
import com.github.blanexie.vxph.tracker.hikariDataSource
import com.github.blanexie.vxph.tracker.toEntity
import org.slf4j.LoggerFactory
import java.time.LocalDateTime


class PeerEntity() {
    lateinit var passkey: String
    lateinit var peerId: String
    lateinit var infoHash: String
    lateinit var remoteAddress: String
    var port: Int? = null
    var downloaded: Long = 0
    var left: Long = 0
    var uploaded: Long = 0
    var event: String? = null
    lateinit var createTime: LocalDateTime
    lateinit var updateTime: LocalDateTime
    var status: Int = 0

    constructor(
        passkey: String, peerId: String, infoHash: String, remoteAddress: String,
        port: Int?, downloaded: Long, left: Long, uploaded: Long, event: String?,
        createTime: LocalDateTime, updateTime: LocalDateTime, status: Int,
    ) : this() {
        this.passkey = passkey
        this.peerId = peerId
        this.infoHash = infoHash
        this.remoteAddress = remoteAddress
        this.port = port
        this.downloaded = downloaded
        this.left = left
        this.uploaded = uploaded
        this.event = event
        this.createTime = createTime
        this.updateTime = updateTime
        this.status = status
    }

    private val log = LoggerFactory.getLogger(this::class.java)

    fun upsert() {
        val entity = BeanUtil.beanToMap(this).toEntity("peer")
        Db.use(hikariDataSource())
            .upsert(entity, "passkey", "peerId", "infoHash")
    }


    companion object {
        fun findByInfoHash(infoHash: String): List<PeerEntity> {
            val entity = Entity.create("peer").set("infoHash", infoHash)
            return Db.use(hikariDataSource()).find(entity, PeerEntity::class.java)
        }
    }

}