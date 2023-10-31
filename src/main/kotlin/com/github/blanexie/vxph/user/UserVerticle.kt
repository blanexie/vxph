package com.github.blanexie.vxph.user

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
        val userId = request.getParam("userId")
        val userEntity = UserEntity.findById(userId.toLong())
        val accountEntity = AccountEntity.findByUserId(userId.toLong())
        userEntity!!.password = ""
        return R.success().add("userInfo", userEntity)
            .add("account", accountEntity!!)
    }





}