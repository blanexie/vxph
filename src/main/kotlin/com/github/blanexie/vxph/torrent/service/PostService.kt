package com.github.blanexie.vxph.torrent.service

import com.github.blanexie.vxph.torrent.controller.dto.PostQuery
import com.github.blanexie.vxph.torrent.controller.dto.PostReq
import com.github.blanexie.vxph.torrent.entity.Post
import com.github.blanexie.vxph.torrent.entity.Torrent
import com.github.blanexie.vxph.user.entity.User
import org.springframework.data.domain.Page


interface PostService {

    fun findByPostId(postId: Long): Post?

    fun publish(postId: Long)

    fun saveOrUpdate(postReq: PostReq, torrents: List<Torrent>, loginUser: User): Post

    fun query(postQuery: PostQuery): Page<Post>

}