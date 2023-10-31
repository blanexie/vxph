package com.github.blanexie.vxph.email

import com.github.blanexie.vxph.core.Verticle
import com.github.blanexie.vxph.core.getProperty
import io.vertx.ext.mail.MailClient
import io.vertx.ext.mail.MailConfig
import io.vertx.ext.mail.StartTLSOptions
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.slf4j.LoggerFactory

private const val emailEventChannel = "emailEventChannel"

/**
 * 不支持附件
 */
@Verticle
class EmailVerticle : CoroutineVerticle() {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val mailClient: MailClient

    init {
        val config = MailConfig()
        val hostName: String = getProperty("vxph.email.hostname")!!
        val port: Int = getProperty("vxph.email.port")!!
        val username: String = getProperty("vxph.email.username")!!
        val password: String = getProperty("vxph.email.password")!!
        config.setHostname(hostName)
        config.setPort(port)
        config.setStarttls(StartTLSOptions.REQUIRED)
        config.setUsername(username)
        config.setPassword(password)
        mailClient = MailClient.createShared(vertx, config, "163.email")
    }

    override suspend fun start() {
        receiveEmailEvent(mailClient)
    }

    override suspend fun stop() {
        mailClient.close()
    }

    private suspend fun receiveEmailEvent(mailClient: MailClient) {
        vertx.eventBus().consumer(emailEventChannel) {
            val emailEvent = it.toEmailEvent()
            emailEvent.send(mailClient)
                .onSuccess {
                    log.info("send email success, mail message:{}", emailEvent)
                }
                .onFailure { r ->
                    log.error("send mail fail, mail message:{}", emailEvent, r)
                }
        }
    }

}