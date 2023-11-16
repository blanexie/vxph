package com.github.blanexie.vxph.common.exception

enum class SysCode(val code: Int, val msg: String) {

    NotLoginError(403, "请登录后访问"),
    ServerError(500, "Server Error"),
    LongNameAndPwdError(600, "用户名或者密码错误"),
    AliyunClientError(610, "阿里云DDNS错误"),
    RecordIdExist(611, "解析记录已经存在"),
    RemoteIpError(612, "错误的远端ip地址，无法识别"),
    IpError(613, "错误的ip地址，无法解析"),
    PostNotExist(614, "帖子不存在"),
    TorrentNotExist(615, "Torrent不存在"),
    UserNotExist(616, "用户已经不存在了"),
}