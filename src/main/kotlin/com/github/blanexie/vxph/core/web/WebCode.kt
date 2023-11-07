package com.github.blanexie.vxph.core.web


enum class WebCode(val code: Int, val message: String) {
    No_Permission(403, "权限不足"),
    Login_Time_Expire(405, "login time expire"),
    User_Not_Found(406, "未找到用户"),
    User_Name_Password_Error(407, "用户名或者密码错误"),
    Token_Error(408, "Token错误"),
    User_Name_Exist(409, "用户名已经存在"),
    User_Email_Exist(410, "邮箱已经存在"),

    Invite_Code_Error(410, "邀请码错误或者失效"),
    Invite_User_Exist(411, "用户存在生效中的邀请，不要重复发邀"),
    Password_Repeat_Error(412, "两次密码不一致"),
    Signature_Check_Error(413, "签名校验没通过"),
}
