package com.github.blanexie.vxph

import com.github.blanexie.vxph.core.loadAnnotationClass
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.CoroutineVerticle

class MainVerticle : CoroutineVerticle() {
    override suspend fun start() {
        loadAnnotationClass(this::class.java.packageName, vertx)
    }
}


fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(MainVerticle()).onFailure {
        it.printStackTrace()
    }.onSuccess {

    }
}