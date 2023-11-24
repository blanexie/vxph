package com.github.blanexie.vxph.common.entity

import com.github.blanexie.vxph.common.exception.SysCode

class WebResp(val code: Int, val message: String, val data: Any?) {

    fun add(key: String, value: Any): WebResp {
        val hashMap = data as HashMap<String, Any>
        hashMap[key] = value
        return this
    }

    companion object {
        fun ok(): WebResp {
            return WebResp(code = 200, message = "success", hashMapOf<String, Any>())
        }

        fun ok(body: Any): WebResp {
            return WebResp(code = 200, message = "success", body)
        }

        fun fail(sysCode: SysCode): WebResp {
            return WebResp(code = sysCode.code, message = sysCode.msg, hashMapOf<String, Any>())
        }

        fun fail(sysCode: SysCode, message: String): WebResp {
            return WebResp(code = sysCode.code, message = message, hashMapOf<String, Any>())
        }
    }
}