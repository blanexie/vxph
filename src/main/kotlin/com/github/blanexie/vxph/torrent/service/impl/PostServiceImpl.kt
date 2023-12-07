package com.github.blanexie.vxph.torrent.service.impl

import cn.hutool.core.util.StrUtil
import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.exception.VxphException
import com.github.blanexie.vxph.torrent.controller.dto.PostQuery
import com.github.blanexie.vxph.torrent.controller.dto.PostReq
import com.github.blanexie.vxph.torrent.entity.Post
import com.github.blanexie.vxph.torrent.repository.LabelRepository
import com.github.blanexie.vxph.torrent.repository.PostRepository
import com.github.blanexie.vxph.torrent.service.FileResourceService
import com.github.blanexie.vxph.torrent.service.PostService
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class PostServiceImpl(
    private val postRepository: PostRepository,
    private val entityManager: EntityManager,
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

    override fun saveOrUpdate(postReq: PostReq, loginUser: Long): Post {
        return if (postReq.id != null) {
            updatePost(postReq, loginUser)
        } else {
            savePost(postReq, loginUser)
        }
    }

    override fun query(postQuery: PostQuery): Page<Post> {
        var hql = "from Post p"
        var pageHql = "select count(*) from Post"
        val params = hashMapOf<String, Any>()
        if (StrUtil.isNotBlank(postQuery.keyword)) {
            hql = "$hql where p.title like :keyword"
            pageHql = "$pageHql where p.title like :keyword"
            params["keyword"] = postQuery.keyword!!
        }
        val createQuery = entityManager.createQuery(hql)
        createQuery.firstResult = (postQuery.page - 1) * postQuery.pageSize
        createQuery.maxResults = postQuery.pageSize
        val resultList = createQuery.resultList as List<Post>

        val pageQuery = entityManager.createQuery(pageHql)
        val total = pageQuery.singleResult as Long
        return PageImpl(resultList, PageRequest.of(postQuery.page, postQuery.pageSize), total)
    }

    private fun savePost(postReq: PostReq, loginUser: Long): Post {
        val post = Post(
            postReq.id, postReq.title, postReq.coverImg, loginUser, postReq.imgs, emptyList(), postReq.type, postReq.labels,
            postReq.markdown
        )
        return postRepository.save(post)
    }

    private fun updatePost(postReq: PostReq, loginUser: Long): Post {
        val post = postRepository.findById(postReq.id!!).orElseThrow { VxphException(SysCode.PostNotExist) }
        post.title = postReq.title
        post.coverImg = postReq.coverImg
        post.imgs = postReq.imgs
        post.owner = loginUser
        post.markdown = postReq.markdown
        post.type = postReq.type
        post.labels = postReq.labels
        return postRepository.save(post)
    }

}