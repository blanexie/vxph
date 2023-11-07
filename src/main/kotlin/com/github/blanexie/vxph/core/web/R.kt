package com.github.blanexie.vxph.core.web


class R(
    val code: Int,
    val error: String,
    val body: HashMap<String, Any>
) {

    fun add(key: String, v: Any): R {
        this.body[key] = v
        return this
    }

    companion object {
        fun success(data: Any): R {
            val r = R(code = 200, error = "", body = hashMapOf())
            return r.add("data", data)
        }

        fun success(): R {
            return R(code = 200, error = "", body = hashMapOf())
        }

        fun fail(code: WebCode): R {
            return R(code = code.code, error = code.message, body = hashMapOf())
        }

        fun fail(code: WebCode, error: String): R {
            return R(code = code.code, error = error, body = hashMapOf())
        }
    }
}


