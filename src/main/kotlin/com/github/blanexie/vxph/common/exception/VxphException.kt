package com.github.blanexie.vxph.common.exception

class VxphException(val sysCode: SysCode, val msg: String) : RuntimeException("${sysCode.code}; $msg") {
    constructor(sysCode: SysCode) : this(sysCode, sysCode.msg)


}