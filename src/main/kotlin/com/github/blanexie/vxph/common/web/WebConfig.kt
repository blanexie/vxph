package com.github.blanexie.vxph.common.web

import cn.dev33.satoken.context.SaHolder
import cn.dev33.satoken.interceptor.SaInterceptor
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.github.blanexie.vxph.common.satoken.SaTokenCheckPermission
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Configuration
class WebConfig(
    private val saTokenCheckPermission: SaTokenCheckPermission
) : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver?>) {
        resolvers.add(0, InfoHashParamResolver())
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        // 注册 Sa-Token 拦截器，定义详细认证规则
        registry.addInterceptor(
            SaInterceptor {
                val request = SaHolder.getRequest()
                if (request.method == "GET" || request.method == "POST") {
                    saTokenCheckPermission.checkPermission()
                }
            }
        ).addPathPatterns("/**")
    }


    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/api/**")  //允许跨域访问的路径
            .allowedOrigins("*")       //允许跨域访问的源
            .allowedMethods("POST", "GET", "OPTIONS") //允许请求的方法
            .maxAge(168000) //预检隔离时间
            .allowedHeaders("*") //允许头部设置
            .allowCredentials(false) //是否发送cookie
    }


    @Bean
    fun jackson2ObjectMapperBuilderCustomizer(): Jackson2ObjectMapperBuilderCustomizer {
        return Jackson2ObjectMapperBuilderCustomizer { jacksonObjectMapperBuilder: Jackson2ObjectMapperBuilder ->
            val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            //返回时间数据序列化
            jacksonObjectMapperBuilder.serializerByType(LocalDateTime::class.java, LocalDateTimeSerializer(dateTimeFormatter))
            //接收时间数据反序列化
            jacksonObjectMapperBuilder.deserializerByType(LocalDateTime::class.java, LocalDateTimeDeserializer(dateTimeFormatter))
            jacksonObjectMapperBuilder.serializerByType(LocalDate::class.java, LocalDateSerializer(dateFormatter))
            //接收时间数据反序列化
            jacksonObjectMapperBuilder.deserializerByType(LocalDate::class.java, LocalDateDeserializer(dateFormatter))
            jacksonObjectMapperBuilder.serializerByType(BigInteger::class.java, ToStringSerializer.instance)
            jacksonObjectMapperBuilder.serializerByType(Long::class.java, ToStringSerializer.instance)
        }
    }

}