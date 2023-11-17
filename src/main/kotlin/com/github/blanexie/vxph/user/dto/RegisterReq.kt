package com.github.blanexie.vxph.user.dto

data class RegisterReq(
    val name: String,
    val email: String,
    val sex: String,
    val password: String,
    val inviteCode: String,
)