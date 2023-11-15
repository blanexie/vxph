package com.github.blanexie.vxph.torrent.controller

import com.github.blanexie.vxph.torrent.entity.Post
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/post")
@RestController
class PostController {

    @PostMapping("/add")
    fun addPost(@RequestBody post: Post) {

    }



}