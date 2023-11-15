package com.github.blanexie.vxph.common.web

import cn.dev33.satoken.interceptor.SaInterceptor
import cn.dev33.satoken.router.SaRouter
import cn.dev33.satoken.router.SaRouterStaff
import cn.dev33.satoken.stp.StpUtil
import com.github.blanexie.vxph.common.satoken.SaTokenContextForSpring
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class WebConfig : WebMvcConfigurer {


    //region 注册自定义HandlerMethodArgumentResolver
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver?>) {
        resolvers.add(0, InfoHashRequestParamResolver())

    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        // 注册 Sa-Token 拦截器，定义详细认证规则
        registry.addInterceptor(SaInterceptor {
            // 指定一条 match 规则
            SaRouter.match("/api/**") // 拦截的 path 列表，可以写多个 */
                //.notMatch("/api/user/login") // 排除掉的 path 列表，可以写多个
                .check { r: SaRouterStaff? -> StpUtil.checkLogin() } // 要执行的校验动作，可以写完整的 lambda 表达式

            // 根据路由划分模块，不同模块不同鉴权
            //   SaRouter.match("/user/**") { r: SaRouterStaff? -> StpUtil.checkPermission("user") }

        }).addPathPatterns("/**")
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**") //允许跨域访问的路径
            .allowedOrigins("*") //允许跨域访问的源
            .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE") //允许请求的方法
            .maxAge(168000) //预检隔离时间
            .allowedHeaders("*") //允许头部设置
            .allowCredentials(false) //是否发送cookie
    }

    @Bean
    fun saTokenContextForSpring(): SaTokenContextForSpring {
        return SaTokenContextForSpring()
    }
}