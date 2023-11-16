package com.github.blanexie.vxph.torrent.service

import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.exception.VxphException
import com.github.blanexie.vxph.torrent.dto.PostReq
import com.github.blanexie.vxph.torrent.entity.FileResource
import com.github.blanexie.vxph.torrent.entity.Post
import com.github.blanexie.vxph.torrent.repository.PostRepository
import com.github.blanexie.vxph.torrent.repository.TorrentRepository
import com.github.blanexie.vxph.user.entity.User
import com.github.blanexie.vxph.user.repository.UserRepository
import com.github.blanexie.vxph.user.service.UserService
import org.springframework.stereotype.Service
import java.util.function.Function
import java.util.stream.Collectors

@Service
class PostService(
    private val postRepository: PostRepository,
    private val fileResourceService: FileResourceService,
    private val torrentService: TorrentService,
) {


    fun findByPostId(postId: Long): Post? {
        val postOptional = postRepository.findById(postId)
        return postOptional.orElse(null)
    }

    fun saveOrUpdate(postReq: PostReq, loginUser: User): Post {
        val hashs = arrayListOf<String>()
        postReq.coverImg?.let { hashs.add(it) }
        postReq.imgs?.forEach { hashs.add(it) }
        val fileResources = fileResourceService.findAllByHashIn(hashs)
        val fileMap = fileResources.stream().collect(Collectors.toMap(FileResource::hash, Function.identity()))
        return if (postReq.id != null) {
            updatePost(postReq, loginUser, fileMap)
        } else {
            savePost(postReq, loginUser, fileMap)
        }
    }

    private fun savePost(postReq: PostReq, loginUser: User, fileMap: MutableMap<String, FileResource>): Post {
        val imgs = postReq.imgs?.mapNotNull { p -> fileMap[p] }?.toList()
        val torrents = postReq.torrent?.let {
            if (it.isNotEmpty()) {
                torrentService.findAllByInfoHashIn(it)
            } else emptyList()
        }
        val post = Post(
            postReq.id, postReq.title, fileMap[postReq.coverImg], loginUser, imgs ?: emptyList(),
            postReq.markdown, torrents ?: emptyList(),
        )
        return postRepository.save(post)
    }

    private fun updatePost(postReq: PostReq, loginUser: User, fileMap: MutableMap<String, FileResource>): Post {
        val post = postRepository.findById(postReq.id!!).orElseThrow { VxphException(SysCode.PostNotExist) }
        post.title = postReq.title
        postReq.coverImg?.let {
            post.coverImg = fileMap[it]
        }
        postReq.imgs?.let {
            post.imgs = it.mapNotNull { p -> fileMap[p] }.toList()
        }
        post.owner = loginUser

        post.markdown = postReq.markdown
        postReq.torrent?.let {
            if (it.isNotEmpty()) {
                post.torrent = torrentService.findAllByInfoHashIn(it)
            }
        }
        return postRepository.save(post)
    }

}