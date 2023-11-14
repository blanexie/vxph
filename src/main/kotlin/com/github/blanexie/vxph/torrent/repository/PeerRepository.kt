package com.github.blanexie.vxph.torrent.repository

import com.github.blanexie.vxph.torrent.entity.Peer
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.repository.query.QueryByExampleExecutor

interface PeerRepository : CrudRepository<Peer, Long>, QueryByExampleExecutor<Peer> {

    fun findByPassKey(passKey: String): Peer?

    fun findByInfoHashAndEventIn(infoHash: String, events: List<String>): List<Peer>

    @Query(" from Peer p where p.left = 0 and p.infoHash in  ( :infoHash ) ")
    fun findByInfoHashIn(@Param("infoHash") infoHash: List<String>):List<Peer>

}