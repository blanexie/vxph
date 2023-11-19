package com.github.blanexie.vxph

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@EnableAsync
@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
class VxphApplication

var context: ConfigurableApplicationContext? = null
fun main(args: Array<String>) {
    context = runApplication<VxphApplication>(*args)
}
