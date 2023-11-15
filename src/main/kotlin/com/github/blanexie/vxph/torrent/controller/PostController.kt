package com.github.blanexie.vxph.torrent.controller

import cn.dev33.satoken.stp.StpUtil
import com.github.blanexie.vxph.common.web.Result
import com.github.blanexie.vxph.torrent.dto.PostReq
import com.github.blanexie.vxph.torrent.entity.Post
import com.github.blanexie.vxph.torrent.service.PostService
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/post")
@RestController
class PostController(
    private val postService: PostService
) {

    @PostMapping("/add")
    fun addPost(@RequestBody postReq: PostReq): Result {
        postReq.owner = StpUtil.getLoginIdAsLong()
        val post = postService.saveOrUpdate(postReq)
        return Result.ok(post)
    }

    @GetMapping("/publish")
    fun publish(@RequestParam postId: Long): Result {
        return Result.ok()
    }

}