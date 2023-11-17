package com.github.blanexie.vxph.mail

import com.github.blanexie.vxph.user.entity.Invite

interface MailService {


    fun sendInviteMail(invite: Invite): Boolean

}