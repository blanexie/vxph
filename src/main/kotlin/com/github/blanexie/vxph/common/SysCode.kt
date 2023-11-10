package com.github.blanexie.vxph.common

enum class SysCode(val code: Int, val msg: String) {

    ServerError(500, "Server Error"),
    LongNameAndPwdError(600, "用户名或者密码错误"),
    AliyunClientError(610, "阿里云DDNS错误"),
    RecordIdExist(611, "解析记录已经存在")

}