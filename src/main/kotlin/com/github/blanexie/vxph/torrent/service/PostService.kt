package com.github.blanexie.vxph.torrent.service

import com.github.blanexie.vxph.torrent.dto.PostReq
import com.github.blanexie.vxph.torrent.entity.FileResource
import com.github.blanexie.vxph.torrent.entity.Post
import com.github.blanexie.vxph.torrent.entity.Torrent
import com.github.blanexie.vxph.torrent.repository.FileResourceRepository
import com.github.blanexie.vxph.torrent.repository.PostRepository
import com.github.blanexie.vxph.torrent.repository.TorrentRepository
import com.github.blanexie.vxph.user.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.function.Function
import java.util.stream.Collectors

@Service
class PostService(
    private val postRepository: PostRepository,
    private val fileResourceRepository: FileResourceRepository,
    private val userRepository: UserRepository,
    private val torrentRepository: TorrentRepository,
) {


    fun findByPostId(postId: Long): Post? {
        val postOptional = postRepository.findById(postId)
        return postOptional.orElse(null)
    }

    fun saveOrUpdate(postReq: PostReq): Post {
        val files = arrayListOf<String>()
        postReq.coverImg?.let { files.add(it) }
        postReq.imgs?.forEach { files.add(it) }
        val fileResources = fileResourceRepository.findAllByHashIn(files)
        val fileMap = fileResources.stream().collect(Collectors.toMap(FileResource::hash, Function.identity()))
        return if (postReq.id != null) {
            updatePost(postReq, fileMap)
        } else {
            savePost(postReq, fileMap)
        }
    }

    private fun savePost(postReq: PostReq, fileMap: MutableMap<String, FileResource>): Post {
        val user = userRepository.findById(postReq.owner!!).get()
        val imgs = postReq.imgs?.mapNotNull { p -> fileMap[p] }?.toList()
        val torrents = postReq.torrent?.let {
            if (it.isNotEmpty()) {
                torrentRepository.findAllByInfoHashIn(it)
            } else emptyList()
        }
        val post = Post(
            postReq.id, postReq.title, fileMap[postReq.coverImg], user, imgs ?: emptyList(),
            postReq.markdown, torrents ?: emptyList(),
        )
        return postRepository.save(post)
    }

    private fun updatePost(postReq: PostReq, fileMap: MutableMap<String, FileResource>): Post {
        val post = postRepository.findById(postReq.id!!).get()
        post.title = postReq.title
        postReq.coverImg?.let {
            post.coverImg = fileMap[it]
        }
        postReq.imgs?.let {
            post.imgs = it.mapNotNull { p -> fileMap[p] }.toList()
        }
        val user = userRepository.findById(postReq.owner!!).get()
        post.owner = user

        post.markdown = postReq.markdown
        postReq.torrent?.let {
            if (it.isNotEmpty()) {
                post.torrent = torrentRepository.findAllByInfoHashIn(it)
            }
        }
        return postRepository.save(post)
    }

}