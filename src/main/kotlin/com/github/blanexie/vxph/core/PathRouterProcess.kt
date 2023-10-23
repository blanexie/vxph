package com.github.blanexie.vxph.core

import cn.hutool.core.lang.Singleton
import cn.hutool.core.util.ClassUtil
import cn.hutool.core.util.ReflectUtil
import cn.hutool.core.util.StrUtil
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.Router
import org.slf4j.LoggerFactory
import java.lang.reflect.Method

/**
 * 路由加载
 */

class PathRouterProcess {

    private val log = LoggerFactory.getLogger(this::class.java)


    /**
     * 组装路由
     */
    fun loadPathRouter(router: Router) {
        val classAndMethods = findPathClass()
        classAndMethods.forEach {
            val reqMethod = it.reqMethod
            val routerR = if (StrUtil.isEmpty(reqMethod)) {
                router.route(it.path)
            } else {
                router.route(HttpMethod.valueOf(reqMethod), it.path)
            }
            routerR.blockingHandler { r ->
                try {
                    log.info(
                        "load router path:{} {}  class:{}   method:{}", it.reqMethod, it.path, it.clazz, it.method
                    )
                    val newInstance = Singleton.get(it.clazz)
                    val response: HttpServerResponse = ReflectUtil.invoke(newInstance, it.method, r.request())
                } catch (e: Throwable) {
                    log.error("path: {} request error", it.path, e)
                    r.response().statusCode = 500
                    r.response().putHeader("content-type", "text/plain; charset=utf-8 ")
                    r.response().send(e.message)
                }

            }
        }

    }


    /**
     * 找到所有的配置类
     */
    private fun findPathClass(): List<ClassAndMethod> {
        val ret = arrayListOf<ClassAndMethod>()
        annotationSet.forEach {
            val prefix = it.getAnnotation(Path::class.java).value
            it.methods.filter { m -> m.getAnnotation(Path::class.java) != null }.forEach { m ->
                val annotation = m.getAnnotation(Path::class.java)
                var path = "$prefix${annotation.value}".replace("//", "/").trim()
                if (path.endsWith("/")) {
                    path = path.removeSuffix("/")
                }
                ret.add(ClassAndMethod(it, m, path, annotation.method))
            }
        }
        return ret
    }

}


data class ClassAndMethod(val clazz: Class<*>, val method: Method, val path: String, val reqMethod: String)


