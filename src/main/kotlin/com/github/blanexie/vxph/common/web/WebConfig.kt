package com.github.blanexie.vxph.common.web

import com.github.blanexie.vxph.common.satoken.SaTokenContextForSpring
import com.github.blanexie.vxph.common.web.InfoHashRequestParamResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class WebConfig : WebMvcConfigurer {


    //region 注册自定义HandlerMethodArgumentResolver
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver?>) {
        resolvers.add(0, InfoHashRequestParamResolver())

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