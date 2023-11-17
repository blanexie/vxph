package com.github.blanexie.vxph.user.service

import com.github.blanexie.vxph.user.entity.Account
import com.github.blanexie.vxph.user.entity.Invite

interface InviteService {

    fun send(email: String, account: Account): Invite

    fun findByCode(code: String): Invite?

    fun checkEmail(code: String, receiveEmail: String): Boolean
}