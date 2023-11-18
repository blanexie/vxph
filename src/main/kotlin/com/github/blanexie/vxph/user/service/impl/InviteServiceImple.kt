package com.github.blanexie.vxph.user.service.impl

import cn.hutool.core.convert.Convert
import cn.hutool.core.util.RandomUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.json.JSONUtil
import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.exception.VxphException
import com.github.blanexie.vxph.mail.entity.Email
import com.github.blanexie.vxph.mail.service.MailService
import com.github.blanexie.vxph.user.InviteMailTemplateCode
import com.github.blanexie.vxph.user.entity.Invite
import com.github.blanexie.vxph.user.entity.User
import com.github.blanexie.vxph.user.repository.InviteRepository
import com.github.blanexie.vxph.user.service.CodeService
import com.github.blanexie.vxph.user.service.InviteService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class InviteServiceImple(
    private val inviteRepository: InviteRepository,
    private val codeService: CodeService,
    private val mailService: MailService,
    @Value("\${spring.mail.username}") private val sendMailName: String,
) : InviteService {

    override fun saveInviteAndSendMail(receiveMail: String, user: User): Invite {
        val code = RandomUtil.randomString(5).uppercase()
        val email = buildInviteMailMessage(code, user, receiveMail)
        mailService.sendMail(email)
        val invite = Invite(null, code, receiveMail, user, null)
        return inviteRepository.save(invite)
    }

    private fun buildInviteMailMessage(code: String, user: User, receiveMail: String): Email {
        val template = codeService.findValueByCode(InviteMailTemplateCode)!!
        val templateJson = JSONUtil.parseObj(template)
        val subject = templateJson["subject"]
        val content =
            StrUtil.format(Convert.toStr(templateJson["content"]), mapOf("code" to code, "name" to user.name))
        return Email(null, Convert.toStr(subject), sendMailName, receiveMail, null, null, content, null, user)
    }

    override fun findByCode(code: String): Invite? {
        return inviteRepository.findByCode(code)
    }

    override fun useInvite(code: String, receiveEmail: String): Invite {
        val invite = inviteRepository.findByCode(code) ?: throw VxphException(SysCode.InvalidInviteCode)
        if (invite.receiveEmail != receiveEmail) {
            throw VxphException(SysCode.InvalidInviteCode)
        }
        if (invite.acceptTime != null) {
            throw VxphException(SysCode.InvalidInviteCode)
        }
        invite.acceptTime= LocalDateTime.now()
        inviteRepository.save(invite)
        return invite
    }
}