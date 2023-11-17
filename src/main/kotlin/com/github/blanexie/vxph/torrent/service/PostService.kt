package com.github.blanexie.vxph.torrent.service

import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.exception.VxphException
import com.github.blanexie.vxph.torrent.dto.PostReq
import com.github.blanexie.vxph.torrent.entity.FileResource
import com.github.blanexie.vxph.torrent.entity.Post
import com.github.blanexie.vxph.torrent.entity.Torrent
import com.github.blanexie.vxph.torrent.repository.PostRepository
import com.github.blanexie.vxph.torrent.repository.TorrentRepository
import com.github.blanexie.vxph.user.entity.User
import com.github.blanexie.vxph.user.repository.UserRepository
import com.github.blanexie.vxph.user.service.UserService
import org.springframework.stereotype.Service
import java.util.function.Function
import java.util.stream.Collectors


interface PostService {

    fun findByPostId(postId: Long): Post?

    fun publish(postId: Long)

    fun saveOrUpdate(postReq: PostReq, torrents: List<Torrent>, loginUser: User): Post

}