package com.github.blanexie.vxph.common

import cn.dev33.satoken.util.SaResult
import com.github.blanexie.vxph.context
import org.springframework.core.env.Environment


fun getProperty(key: String): String? {
    val bean = context!!.getBean(Environment::class.java)
    return bean.getProperty(key)
}

fun SaResult.fail(sysCode: SysCode):SaResult {
    this.code = sysCode.code
    this.msg = sysCode.msg
    return this
}