package com.github.blanexie.vxph.user

import cn.hutool.crypto.digest.DigestUtil
import com.github.blanexie.vxph.core.R
import com.github.blanexie.vxph.core.Verticle
import com.github.blanexie.vxph.core.getProperty
import com.github.blanexie.vxph.core.web.HttpVerticle
import com.github.blanexie.vxph.core.web.Path
import com.github.blanexie.vxph.user.entity.AccountEntity
import com.github.blanexie.vxph.user.entity.UserEntity
import io.vertx.core.http.HttpServerRequest

@Verticle
class UserVerticle : HttpVerticle() {
    private val expireMinutes: Int = getProperty("vxph.http.token.expireMinutes", 10)

    @Path("/user/login")
    fun login(request: HttpServerRequest): R {
        val nickName = request.getParam("nickName")
        val time = request.getParam("timestamp")
        val sign = request.getParam("sign")

        //判断是否超时
        if (System.currentTimeMillis() - time.toLong() > expireMinutes * 30 * 1000L) {
            return R.fail(401, "login time out")
        }

        val userEntity = UserEntity.findByName(nickName) ?: return R.fail(403, "未找到用户信息")
        val accountEntity = AccountEntity.findByUserId(userEntity.id!!)

        val signStr = "${userEntity.id}&${userEntity.password}&${time}"
        val sha256Hex = DigestUtil.sha256Hex(signStr)
        return if (sign == sha256Hex) {
            userEntity.password = ""
            val result = mapOf("time" to time, "sha256" to sha256Hex, "userId" to userEntity.id)
            R.success().add("userInfo", userEntity)
                .add("account", accountEntity!!)
                .add("token", result)
        } else {
            R.fail(403, "用户名或者密码不正确")
        }
    }


}