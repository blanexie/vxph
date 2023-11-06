package com.github.blanexie.vxph.email

import cn.hutool.core.map.MapUtil
import com.github.blanexie.vxph.core.objectMapper
import io.vertx.core.Future
import io.vertx.core.eventbus.Message
import io.vertx.ext.mail.MailClient
import io.vertx.ext.mail.MailMessage
import io.vertx.ext.mail.MailResult

class EmailEvent(
    val type: String,
    val from: String,
    val to: List<String>,
    val cc: List<String>?,
    val subject: String,
    val text: String?,
    val html: String?,
) {

    fun send(mailClient: MailClient): Future<MailResult> {
        val message = MailMessage()
        message.from = this.from
        message.to = this.to
        message.cc = this.cc
        message.subject= this.subject
        message.text = this.text
        message.html = this.html
        return mailClient.sendMail(message)
    }
}


fun Message<String>.toEmailEvent(): EmailEvent {
    val body = this.body()
    val readValue = objectMapper.readValue(body, Map::class.java)
    val channel = MapUtil.get(readValue, "channel", String::class.java)

    val type = MapUtil.get(readValue, "type", String::class.java)
    val data = MapUtil.get(readValue, "data", String::class.java)

    val message = objectMapper.readValue(data, Map::class.java)
    val from = MapUtil.get(message, "from", String::class.java)
    val subject = MapUtil.get(message, "subject", String::class.java)
    val to = MapUtil.get(message, "to", List::class.java)
    val cc = MapUtil.get(message, "cc", List::class.java)
    val text = MapUtil.get(message, "text", String::class.java)
    val html = MapUtil.get(message, "html", String::class.java)

    return EmailEvent(type, from, to as List<String>, cc as List<String>, subject, text, html)
}
