package com.github.blanexie.vxph.user.service

import com.github.blanexie.vxph.user.entity.Invite
import com.github.blanexie.vxph.user.entity.User

interface InviteService {

    fun saveInviteAndSendMail(receiveMail: String, user: User): Invite

    fun findByCode(code: String): Invite?

    fun checkEmail(code: String, receiveEmail: String): Boolean
}