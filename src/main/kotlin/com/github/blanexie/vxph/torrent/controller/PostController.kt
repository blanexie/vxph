package com.github.blanexie.vxph.torrent.controller

import cn.dev33.satoken.stp.StpUtil
import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.web.WebResp
import com.github.blanexie.vxph.torrent.dto.PostReq
import com.github.blanexie.vxph.torrent.service.PostService
import com.github.blanexie.vxph.user.service.UserService
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/post")
@RestController
class PostController(
    private val postService: PostService,
    private val userService: UserService,
) {

    @PostMapping("/add")
    fun addPost(@RequestBody postReq: PostReq): WebResp {
        postReq.owner = StpUtil.getLoginIdAsLong()
        val loginUser = userService.findById(StpUtil.getLoginIdAsLong())
        if (loginUser == null) {
            StpUtil.logout()
            return WebResp.fail(SysCode.UserNotExist)
        }
        val post = postService.saveOrUpdate(postReq, loginUser)
        return WebResp.ok(post)
    }

    @GetMapping("/publish")
    fun publish(@RequestParam postId: Long): WebResp {
        return WebResp.ok()
    }

}