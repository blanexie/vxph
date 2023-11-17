package com.github.blanexie.vxph.torrent.service.impl

import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.exception.VxphException
import com.github.blanexie.vxph.torrent.controller.dto.PostReq
import com.github.blanexie.vxph.torrent.entity.FileResource
import com.github.blanexie.vxph.torrent.entity.Post
import com.github.blanexie.vxph.torrent.entity.Torrent
import com.github.blanexie.vxph.torrent.repository.PostRepository
import com.github.blanexie.vxph.torrent.service.FileResourceService
import com.github.blanexie.vxph.torrent.service.PostService
import com.github.blanexie.vxph.user.entity.User
import org.springframework.stereotype.Service
import java.util.function.Function
import java.util.stream.Collectors

@Service
class PostServiceImpl(
    private val postRepository: PostRepository,
    private val fileResourceService: FileResourceService,
) : PostService {


    override fun findByPostId(postId: Long): Post? {
        val postOptional = postRepository.findById(postId)
        return postOptional.orElse(null)
    }

    override fun publish(postId: Long) {
        val post = postRepository.findById(postId).orElseThrow { VxphException(SysCode.TorrentNotExist) }
        post.publish()
        postRepository.save(post)
    }

    override fun saveOrUpdate(postReq: PostReq, torrents: List<Torrent>, loginUser: User): Post {
        val hashs = arrayListOf<String>()
        postReq.coverImg?.let { hashs.add(it) }
        postReq.imgs?.forEach { hashs.add(it) }
        val fileResources = fileResourceService.findAllByHashIn(hashs)
        val fileMap = fileResources.stream().collect(Collectors.toMap(FileResource::hash, Function.identity()))
        return if (postReq.id != null) {
            updatePost(postReq, torrents, loginUser, fileMap)
        } else {
            savePost(postReq, torrents, loginUser, fileMap)
        }
    }

    private fun savePost(postReq: PostReq, torrents: List<Torrent>, loginUser: User, fileMap: MutableMap<String, FileResource>): Post {
        val imgs = postReq.imgs?.mapNotNull { p -> fileMap[p] }?.toList()
        val post = Post(
            postReq.id, postReq.title, fileMap[postReq.coverImg], loginUser, imgs ?: emptyList(),
            postReq.markdown, torrents
        )
        return postRepository.save(post)
    }

    private fun updatePost(postReq: PostReq, torrents: List<Torrent>, loginUser: User, fileMap: MutableMap<String, FileResource>): Post {
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
        post.torrents = torrents

        return postRepository.save(post)
    }

}