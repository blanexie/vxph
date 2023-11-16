package com.github.blanexie.vxph.user.dto

data class LoginReq(
    val username: String,
    val pwdSha256: String,
    val time: Long,
)