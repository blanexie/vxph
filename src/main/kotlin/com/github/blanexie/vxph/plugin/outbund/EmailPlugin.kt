package com.github.blanexie.vxph.plugin.outbund

import cn.hutool.core.codec.Base64Decoder
import com.github.blanexie.vxph.core.AbstractVerticle
import com.github.blanexie.vxph.core.entity.Message
import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.ext.mail.*
import org.slf4j.LoggerFactory


class EmailPlugin(val hostName: String, val port: Int, val userName: String, val password: String) :
  AbstractVerticle(type = "email", flowId = hostName, id = userName) {

  private val log = LoggerFactory.getLogger(this::class.java)
  private lateinit var mailClient: MailClient
  override suspend fun handleStart() {

    val config = MailConfig()
    config.setHostname(hostName)
    config.setPort(port)
    config.setStarttls(StartTLSOptions.REQUIRED)
    config.setUsername(userName)
    config.setPassword(password)
    mailClient = MailClient.createShared(vertx, config, "$topic-pool")

  }


  override suspend fun handleReceive(message: Message, handler: Handler<Message>) {
    val from = message.data["from"] as String
    val to = message.data["to"] as String
    val cc = message.data["cc"] as List<String>?

    val text = message.data["text"] as String?
    val html = message.data["html"] as String?

    val attachments = message.data["attachment"] as Map<String, String>?
    val inlineAttachments = message.data["inlineAttachment"] as Map<String, String>?

    val mailMessage = MailMessage()
    mailMessage.setFrom(from)
    mailMessage.setTo(to)
    cc?.let { mailMessage.cc = cc }
    text?.let { mailMessage.text = text }
    html?.let { mailMessage.html = html }

    attachments?.let {
      val attachment = MailAttachment.create()
      attachment.setContentType(it["contentType"])
      writeData(it, attachment)
      appendAttachment(mailMessage, attachment)
    }

    inlineAttachments?.let {
      val attachment = MailAttachment.create()
      attachment.setContentType(it["contentType"])
      attachment.setDisposition("inline");
      attachment.setContentId(it["contentId"])
      writeData(it, attachment)
      appendAttachment(mailMessage, attachment)
    }
    val replyMessage = message.toReplyMessage()
    mailClient.sendMail(mailMessage)
      .onSuccess {
        replyMessage.data = mapOf("emailId" to it.messageID, "body" to it.toJson())
        handler.handle(replyMessage)
      }
      .onFailure {
        log.info("send email error ", it)
        replyMessage.data = mapOf("statusCode" to "500", "errorMessage" to it.message)
        handler.handle(replyMessage)
      }
  }

  private fun appendAttachment(mailMessage: MailMessage, attachment: MailAttachment) {
    if (mailMessage.attachment == null) {
      mailMessage.attachment = arrayListOf()
    }
    mailMessage.attachment.add(attachment)
  }

  private fun writeData(it: Map<String, String>, attachment: MailAttachment) {
    val dataType = it["dataType"]
    if (dataType == "String") {
      attachment.setData(Buffer.buffer(it["data"]))
    }
    if (dataType == "base64") {
      attachment.setData(Buffer.buffer(Base64Decoder.decodeStr(it["data"])))
    }
  }


}
