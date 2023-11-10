package com.github.blanexie.vxph.common

class R(
    val code: Int = 200,
    val msg: String = "",
    val body: HashMap<String, Any>? = null
) {

    fun add(key: String, value: Any): R {
        body!![key] = value
        return this
    }

    companion object {
        fun success(): R {
            return R(body = hashMapOf())
        }

        fun success(key: String, value: Any): R {
            return R(body = hashMapOf(key to value))
        }

        fun fail(code: Int, msg: String): R {
            return R(code, msg)
        }

        fun fail(sysCode: SysCode, msg: String): R {
            return R(sysCode.code, msg)
        }

        fun fail(sysCode: SysCode): R {
            return R(sysCode.code, sysCode.msg)
        }
    }
}