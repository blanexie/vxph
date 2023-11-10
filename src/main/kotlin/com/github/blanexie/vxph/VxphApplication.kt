package com.github.blanexie.vxph

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class VxphApplication {
}

var context: ConfigurableApplicationContext? = null
fun main(args: Array<String>) {
    context = runApplication<VxphApplication>(*args)
}
