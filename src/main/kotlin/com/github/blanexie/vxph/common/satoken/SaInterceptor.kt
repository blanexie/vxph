package com.github.blanexie.vxph.common.satoken

import cn.dev33.satoken.annotation.SaIgnore
import cn.dev33.satoken.exception.BackResultException
import cn.dev33.satoken.exception.StopMatchException
import cn.dev33.satoken.`fun`.SaParamFunction
import cn.dev33.satoken.strategy.SaStrategy
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

/**
 * @param auth 认证函数，每次请求执行
 */
class SaInterceptor(var auth: SaParamFunction<Any>) : HandlerInterceptor {

    /**
     * 是否打开注解鉴权，配置为 true 时注解鉴权才会生效，配置为 false 时，即使写了注解也不会进行鉴权
     */
    var isAnnotation = true

    /**
     * 认证函数：每次请求执行
     *
     *  参数：路由处理函数指针
     */
    //  var auth: SaParamFunction<Any> = SaParamFunction<Any> { handler: Any? -> }

    /**
     * 创建一个 Sa-Token 综合拦截器，默认带有注解鉴权能力
     */
    constructor() : this(SaParamFunction<Any> { })


    /**
     * 设置是否打开注解鉴权：配置为 true 时注解鉴权才会生效，配置为 false 时，即使写了注解也不会进行鉴权
     * @param isAnnotation /
     * @return 对象自身
     */
    fun isAnnotation(isAnnotation: Boolean): SaInterceptor {
        this.isAnnotation = isAnnotation
        return this
    }

    /**
     * 写入 [ 认证函数 ]: 每次请求执行
     * @param auth /
     * @return 对象自身
     */
    fun setAuth(auth: SaParamFunction<Any>): SaInterceptor {
        this.auth = auth
        return this
    }


    // ----------------- 验证方法 -----------------

    // ----------------- 验证方法 -----------------
    /**
     * 每次请求之前触发的方法
     */
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        try {

            // 这里必须确保 handler 是 HandlerMethod 类型时，才能进行注解鉴权
            if (isAnnotation && handler is HandlerMethod) {

                // 获取此请求对应的 Method 处理函数
                val method = handler.method

                // 如果此 Method 或其所属 Class 标注了 @SaIgnore，则忽略掉鉴权
                if (SaStrategy.instance.isAnnotationPresent.apply(method, SaIgnore::class.java)) {
                    // 注意这里直接就退出整个鉴权了，最底部的 auth.run() 路由拦截鉴权也被跳出了
                    return true
                }

                // 执行注解鉴权
                SaStrategy.instance.checkMethodAnnotation.accept(method)
            }

            // Auth 路由拦截鉴权校验
            auth.run(handler)
        } catch (e: StopMatchException) {
            // StopMatchException 异常代表：停止匹配，进入Controller
        } catch (e: BackResultException) {
            // BackResultException 异常代表：停止匹配，向前端输出结果
            // 		请注意此处默认 Content-Type 为 text/plain，如果需要返回 JSON 信息，需要在 back 前自行设置 Content-Type 为 application/json
            // 		例如：SaHolder.getResponse().setHeader("Content-Type", "application/json;charset=UTF-8");
            if (response.contentType == null) {
                response.contentType = "text/plain; charset=utf-8"
            }
            response.writer.print(e.message)
            return false
        }
        // 通过验证
        return true
    }
}