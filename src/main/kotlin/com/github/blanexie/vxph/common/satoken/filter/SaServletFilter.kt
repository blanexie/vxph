/*
 * Copyright 2020-2099 sa-token.cc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.blanexie.vxph.common.satoken.filter

import cn.dev33.satoken.error.SaSpringBootErrorCode
import cn.dev33.satoken.exception.BackResultException
import cn.dev33.satoken.exception.SaTokenException
import cn.dev33.satoken.exception.StopMatchException
import cn.dev33.satoken.filter.SaFilter
import cn.dev33.satoken.filter.SaFilterAuthStrategy
import cn.dev33.satoken.filter.SaFilterErrorStrategy
import cn.dev33.satoken.`fun`.SaParamFunction
import cn.dev33.satoken.router.SaRouter
import cn.dev33.satoken.router.SaRouterStaff
import cn.dev33.satoken.util.SaTokenConsts
import jakarta.servlet.*
import org.springframework.core.annotation.Order
import java.io.IOException
import java.util.*

/**
 * Servlet 全局鉴权过滤器
 *
 *
 * 默认优先级为 -100，尽量保证在其它过滤器之前执行
 *
 *
 * @author click33
 * @since 1.19.0
 */
@Order(SaTokenConsts.ASSEMBLY_ORDER)
class SaServletFilter : SaFilter, Filter {
    // ------------------------ 设置此过滤器 拦截 & 放行 的路由 
    /**
     * 拦截路由
     */
    var includeList: MutableList<String> = ArrayList()

    /**
     * 放行路由
     */
    var excludeList: MutableList<String> = ArrayList()
    override fun addInclude(vararg paths: String): SaServletFilter {
        includeList.addAll(Arrays.asList(*paths))
        return this
    }

    override fun addExclude(vararg paths: String): SaServletFilter {
        excludeList.addAll(Arrays.asList(*paths))
        return this
    }

    override fun setIncludeList(pathList: MutableList<String>): SaServletFilter {
        includeList = pathList
        return this
    }

    override fun setExcludeList(pathList: MutableList<String>): SaServletFilter {
        excludeList = pathList
        return this
    }
    // ------------------------ 钩子函数
    /**
     * 认证函数：每次请求执行
     */
    var auth = SaFilterAuthStrategy { r: Any? -> }

    /**
     * 异常处理函数：每次[认证函数]发生异常时执行此函数
     */
    var error = SaFilterErrorStrategy { e: Throwable? -> throw SaTokenException(e).setCode(SaSpringBootErrorCode.CODE_20105) }

    /**
     * 前置函数：在每次[认证函数]之前执行
     * **注意点：前置认证函数将不受 includeList 与 excludeList 的限制，所有路由的请求都会进入 beforeAuth**
     */
    var beforeAuth = SaFilterAuthStrategy { r: Any? -> }
    override fun setAuth(auth: SaFilterAuthStrategy): SaServletFilter {
        this.auth = auth
        return this
    }

    override fun setError(error: SaFilterErrorStrategy): SaServletFilter {
        this.error = error
        return this
    }

    override fun setBeforeAuth(beforeAuth: SaFilterAuthStrategy): SaServletFilter {
        this.beforeAuth = beforeAuth
        return this
    }

    // ------------------------ doFilter
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        try {
            // 执行全局过滤器
            beforeAuth.run(null)
            SaRouter.match(includeList).notMatch(excludeList).check(SaParamFunction<SaRouterStaff> { r: SaRouterStaff? -> auth.run(null) })
        } catch (e: StopMatchException) {
            // StopMatchException 异常代表：停止匹配，进入Controller
        } catch (e: Throwable) {
            // 1. 获取异常处理策略结果 
            val result = if (e is BackResultException) e.message else error.run(e).toString()

            // 2. 写入输出流
            // 		请注意此处默认 Content-Type 为 text/plain，如果需要返回 JSON 信息，需要在 return 前自行设置 Content-Type 为 application/json
            // 		例如：SaHolder.getResponse().setHeader("Content-Type", "application/json;charset=UTF-8");
            if (response.contentType == null) {
                response.contentType = SaTokenConsts.CONTENT_TYPE_TEXT_PLAIN
            }
            response.writer.print(result)
            return
        }

        // 执行 
        chain.doFilter(request, response)
    }

    override fun init(filterConfig: FilterConfig) {}
    override fun destroy() {}
}
