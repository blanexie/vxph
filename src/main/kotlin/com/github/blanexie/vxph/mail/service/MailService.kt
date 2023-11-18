package com.github.blanexie.vxph.mail.service

import com.github.blanexie.vxph.mail.entity.Email
import java.util.concurrent.Future

interface MailService {
    fun sendMail(email: Email)
}