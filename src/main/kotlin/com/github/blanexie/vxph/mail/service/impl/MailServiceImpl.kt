package com.github.blanexie.vxph.mail.service.impl

import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.exception.VxphException
import com.github.blanexie.vxph.mail.entity.Email
import com.github.blanexie.vxph.mail.repository.EmailRepository
import com.github.blanexie.vxph.mail.service.MailService
import jakarta.mail.internet.MimeMessage
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.io.File


@Service
class MailServiceImpl(
    private val mailSender: JavaMailSender,
    private val emailRepository: EmailRepository,
) : MailService {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun sendMail(email: Email)    {
        try {
            val message: MimeMessage = mailSender.createMimeMessage()
            val messageHelper = MimeMessageHelper(message, true)
            //邮件发送人
            messageHelper.setFrom(email.from)
            //邮件接收人
            messageHelper.setTo(email.to)
            //邮件主题
            message.subject = email.subject
            //邮件内容
            email.html?.let {
                messageHelper.setText(email.html, true)
            } ?: messageHelper.setText(email.text!!, false)
            email.file?.let {
                //添加附件
                messageHelper.addAttachment(it, File(it))
            }
            //发送
            mailSender.send(message)
            emailRepository.save(email)

        } catch (e: MailException) {
            log.error("发送邮件异常", e)
            throw VxphException(SysCode.SendEmailError,e.message?:"")
        }
    }
}