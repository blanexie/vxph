package com.github.blanexie.vxph.plugin.inbund

import cn.hutool.cron.CronUtil
import cn.hutool.cron.task.Task
import com.github.blanexie.vxph.core.AbstractVerticle
import com.github.blanexie.vxph.core.entity.Message
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.concurrent.Callable

class CronSchedulePlugin(private val cron: String) :
  AbstractVerticle("cronSchedule", cron, "_") {

  private val log = LoggerFactory.getLogger(this::class.java)

  private val topicSet = mutableSetOf<String>()


  override suspend fun handleEnd() {
    val task = Task() {
      launch {
        val time = LocalDateTime.now().toString()
        topicSet.forEach { receiver ->
          sendMessage(Message(topic, receiver, mapOf("cron" to cron, "time" to time))) {
            log.info(" cron fired success,  replyMessage: ${it.sender}")
          }
        }
      }
    }

    val callable = Callable() {
      CronUtil.schedule(cron, task)
      CronUtil.setMatchSecond(true)
      CronUtil.start()
    }
    vertx.executeBlocking(callable)
  }

  /**
   * 接受初始化消息
   */
  override suspend fun handleReceiveSync(message: Message): Message {
    log.info("handleReceive message: $message")
    val topic = message.data["topic"] as String
    val method = message.data["method"] as String
    if (method == "add") topicSet.add(topic)
    if (method == "remove") topicSet.remove(topic)
    return message.toReplyMessage()
  }


}

