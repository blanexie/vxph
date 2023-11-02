package com.github.blanexie.vxph.user

import cn.hutool.crypto.digest.DigestUtil
import com.github.blanexie.vxph.core.R
import com.github.blanexie.vxph.core.Verticle
import com.github.blanexie.vxph.core.web.HttpVerticle
import com.github.blanexie.vxph.core.web.Path
import com.github.blanexie.vxph.user.entity.AccountEntity
import com.github.blanexie.vxph.user.entity.UserEntity
import io.vertx.core.http.HttpServerRequest

@Verticle
class UserVerticle : HttpVerticle() {

    @Path("/user/login")
    fun login(request: HttpServerRequest): R {
        val nickName = request.getParam("nickName")
        val userEntity = UserEntity.findByName(nickName) ?: return R.fail(403, "未找到用户信息")
        val accountEntity = AccountEntity.findByUserId(userEntity.id!!)

        val time = System.currentTimeMillis()
        val signStr = "${userEntity.id}&${userEntity.password}&${time}"
        val sha256Hex = DigestUtil.sha256Hex(signStr)
        val token = mapOf("time" to time, "sha256" to sha256Hex, "userId" to userEntity.id)

        userEntity.password = ""
        return R.success().add("userInfo", userEntity)
            .add("account", accountEntity!!)
            .add("token", token)
    }


}