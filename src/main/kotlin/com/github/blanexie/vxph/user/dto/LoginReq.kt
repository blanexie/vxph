package com.github.blanexie.vxph.user.dto

data class LoginReq(
    val username: String,
    val password: String,
    val time: Long,
)