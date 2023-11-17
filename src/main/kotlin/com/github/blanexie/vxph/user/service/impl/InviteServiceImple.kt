package com.github.blanexie.vxph.user.service.impl

import cn.hutool.core.util.IdUtil
import cn.hutool.core.util.RandomUtil
import com.github.blanexie.vxph.mail.MailService
import com.github.blanexie.vxph.user.entity.Account
import com.github.blanexie.vxph.user.entity.Invite
import com.github.blanexie.vxph.user.repository.InviteRepository
import com.github.blanexie.vxph.user.service.InviteService
import org.springframework.stereotype.Service

@Service
class InviteServiceImple(
    private val inviteRepository: InviteRepository,
) : InviteService {

    override fun send(email: String, account: Account): Invite {
        val invite = Invite(null, RandomUtil.randomString(5), email, account, null)
        val saveInvite = inviteRepository.save(invite)
        account.invites.add(saveInvite)
        return saveInvite
    }

    override fun findByCode(code: String): Invite? {
        return inviteRepository.findByCode(code)
    }

    override fun checkEmail(code: String, receiveEmail: String): Boolean {
        val invite = inviteRepository.findByCode(code) ?: return false
        return invite.receiveEmail == receiveEmail
    }
}