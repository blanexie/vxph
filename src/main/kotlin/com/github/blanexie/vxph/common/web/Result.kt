package com.github.blanexie.vxph.common.web

import com.github.blanexie.vxph.common.exception.SysCode

class Result(val code: Int, val message: String, val data: Any?) {


    fun add(key: String, value: Any): Result {
        val hashMap = data as HashMap<String, Any>
        hashMap[key] = value
        return this
    }

    companion object {
        fun ok(): Result {
            return Result(code = 200, message = "success", hashMapOf<String, Any>())
        }

        fun ok(body: Any): Result {
            return Result(code = 200, message = "success", body)
        }

        fun fail(sysCode: SysCode): Result {
            return Result(code = sysCode.code, message = sysCode.msg, hashMapOf<String, Any>())
        }

        fun fail(sysCode: SysCode, message: String): Result {
            return Result(code = sysCode.code, message = message, hashMapOf<String, Any>())
        }
    }
}