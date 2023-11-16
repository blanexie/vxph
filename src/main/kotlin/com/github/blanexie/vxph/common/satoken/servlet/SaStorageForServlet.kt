package com.github.blanexie.vxph.common.satoken.servlet

import cn.dev33.satoken.context.model.SaStorage
import jakarta.servlet.http.HttpServletRequest

class SaStorageForServlet(val request: HttpServletRequest) : SaStorage {

    override fun getSource(): Any {
        return request
    }

    override fun get(key: String?): Any? {
        return request.getAttribute(key)
    }

    override fun set(key: String?, value: Any?): SaStorage {
        request.setAttribute(key, value)
        return this
    }

    override fun delete(key: String?): SaStorage {
        request.removeAttribute(key)
        return this
    }


}