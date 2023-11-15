package com.github.blanexie.vxph.torrent.repository

import com.github.blanexie.vxph.torrent.entity.Post
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.QueryByExampleExecutor

interface PostRepository : CrudRepository<Post, Long>, QueryByExampleExecutor<Post> {
}