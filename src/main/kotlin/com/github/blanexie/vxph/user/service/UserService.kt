package com.github.blanexie.vxph.user.service

import com.github.blanexie.vxph.user.entity.User

interface UserService {

    fun login(name: String, pwdSha256: String, time: Long): User?

    fun findById(userId: Long): User?

}