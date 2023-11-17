package com.github.blanexie.vxph.common.exception

class VxphException(val sysCode: SysCode, msg: String) : RuntimeException(msg) {
    constructor(sysCode: SysCode) : this(sysCode, sysCode.msg)

}