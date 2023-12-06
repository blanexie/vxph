package com.github.blanexie.vxph.torrent.repository

import com.github.blanexie.vxph.torrent.entity.Torrent
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.QueryByExampleExecutor

interface TorrentRepository : CrudRepository<Torrent, Long>, QueryByExampleExecutor<Torrent> {

    fun findAllByInfoHashIn(infoHash: List<String>): List<Torrent>

    fun findByInfoHash(infoHash: String):Torrent?

    @Query(" from Torrent  where post.id in (:postIds)")
    fun findAllByPostIn(postIds:List<Long>):List<Torrent>

    @Query("update Torrent  set incomplete=:incomplete , complete=:complete , downloaded=:downloaded where infoHash=:infoHash")
    fun updateData(
        incomplete: Int,
        complete: Int,
        downloaded: Int,
        infoHash: String,
    )


}