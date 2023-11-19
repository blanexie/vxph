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
    TorrentExist(615, "Torrent已经存在"),
    UserNotExist(616, "用户已经不存在了"),
    NotAllowFile(617, "不允许上传的文件类型"),
    UserExist(618, "用户已经存在了"),
    PasswordTooShort(619, "密码太简单了"),
    InvalidInviteCode(620, "无效的邀请码"),
    SendEmailError(621, "邮件发送失败"),
    InvalidNotHave(622, "邀请函数量不足"),
    RoleNotExist(623, "角色不存在"),
    PermissionNotExist(624, "权限不存在"),
}