package com.github.blanexie.vxph.torrent.repository

import com.github.blanexie.vxph.torrent.entity.Peer
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.repository.query.QueryByExampleExecutor
import java.time.LocalDateTime

interface PeerRepository : CrudRepository<Peer, Long>, QueryByExampleExecutor<Peer> {

    fun findByPassKey(passKey: String): Peer?

    fun findByInfoHashAndEventIn(infoHash: String, events: List<String>): List<Peer>

    fun findAllByInfoHash(infoHash: String): List<Peer>

    fun findByInfoHashAndOwner(infoHash: String, owner: Long): Peer?

    @Query("select distinct p.infoHash from Peer p where  p.uploadTime > :startTime")
    fun findInfoHashAfter(@Param("startTime") startTime: LocalDateTime): List<String>

}