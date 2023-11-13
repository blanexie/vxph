package com.github.blanexie.vxph.torrent.repository

import com.github.blanexie.vxph.torrent.entity.Torrent
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.QueryByExampleExecutor

interface TorrentRepository : CrudRepository<Torrent, Long>, QueryByExampleExecutor<Torrent> {
}