package com.github.blanexie.vxph.plugin.outbund

import cn.hutool.core.codec.Base64Decoder
import com.github.blanexie.vxph.core.AbstractVerticle
import com.github.blanexie.vxph.core.entity.Message
import com.github.blanexie.vxph.core.objectMapper
import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.ext.mail.*
import org.slf4j.LoggerFactory


class EmailPlugin(
    val hostName: String, val port: Int, val userName: String, val password: String
) : AbstractVerticle(type = "email", flowId = hostName, id = userName) {

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
        val emailStr = message.data["email"] as String
        val mailMessage = objectMapper.readValue(emailStr, MailMessage::class.java)
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


}
