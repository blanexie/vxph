package com.github.blanexie.vxph.torrent.controller

import cn.dev33.satoken.stp.StpUtil
import com.github.blanexie.vxph.common.entity.WebResp
import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.torrent.controller.dto.PostQuery
import com.github.blanexie.vxph.torrent.controller.dto.PostReq
import com.github.blanexie.vxph.torrent.service.PostService
import com.github.blanexie.vxph.user.service.UserService
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/post")
@RestController
class PostController(
    private val postService: PostService,
) {

    @GetMapping("/findById")
    fun findById(@RequestParam id: Long): WebResp {
        val post = postService.findByPostId(id)?:return  WebResp.fail(SysCode.PostNotExist)
        return WebResp.ok(post)
    }

    @PostMapping("/save")
    fun addPost(@RequestBody postReq: PostReq): WebResp {
        val post = postService.saveOrUpdate(postReq, StpUtil.getLoginIdAsLong())
        return WebResp.ok(post)
    }

    @GetMapping("/publish")
    fun publish(@RequestParam postId: Long): WebResp {
        postService.publish(postId)
        return WebResp.ok()
    }

    @PostMapping("/query")
    fun query(@RequestBody postQuery: PostQuery): WebResp {
        val query = postService.query(postQuery)
        return WebResp.ok(query)
    }


}