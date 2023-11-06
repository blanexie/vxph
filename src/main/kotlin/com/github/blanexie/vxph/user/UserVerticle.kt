package com.github.blanexie.vxph.user

import cn.hutool.core.collection.ListUtil
import cn.hutool.core.convert.Convert
import cn.hutool.core.util.RandomUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.crypto.digest.DigestUtil
import com.github.blanexie.vxph.core.*
import com.github.blanexie.vxph.core.web.HttpVerticle
import com.github.blanexie.vxph.core.web.Path
import com.github.blanexie.vxph.email.EmailEvent
import com.github.blanexie.vxph.user.entity.AccountEntity
import com.github.blanexie.vxph.user.entity.InviteEntity
import com.github.blanexie.vxph.user.entity.UserEntity
import io.vertx.core.http.HttpServerRequest
import io.vertx.ext.mail.MailClient
import io.vertx.ext.mail.MailResult
import io.vertx.kotlin.coroutines.awaitResult
import org.slf4j.LoggerFactory
import java.time.LocalDateTime


val inviteMsgTemplate = """
    如果你没有注册的需求，请忽略。 
    
    邀请你的用户的用户名：{} ； 邀请码：{} ； 
    
    请尽快注册，否则失效
""".trimIndent()


@Verticle
class UserVerticle : HttpVerticle() {
    private val expireMinutes: Int = getProperty("vxph.http.token.expireMinutes", 10)


    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * 登录
     */
    @Path("/user/login")
    fun login(request: HttpServerRequest): R {
        val nickName = request.getParam("nickName")
        val time = request.getParam("timestamp")
        val sign = request.getParam("sign")

        //判断是否超时
        if (System.currentTimeMillis() - time.toLong() > expireMinutes * 30 * 1000L) {
            return R.fail(WebCode.Login_Time_Expire)
        }

        val userEntity = UserEntity.findByName(nickName) ?: return R.fail(WebCode.User_Not_Found)
        val accountEntity = AccountEntity.findByUserId(userEntity.id!!)

        val signStr = "${userEntity.name}&${userEntity.password}&${time}"
        val sha256Hex = DigestUtil.sha256Hex(signStr)
        return if (sign == sha256Hex) {
            userEntity.password = ""
            val result = mapOf("time" to time, "sha256" to sha256Hex, "userId" to userEntity.id)
            R.success().add("userInfo", userEntity)
                .add("account", accountEntity!!)
                .add("token", result)
        } else {
            R.fail(WebCode.User_Name_Password_Error)
        }
    }


    /**
     *
     */
    @Path("/user/sendInvite")
    suspend fun sendInvite(request: HttpServerRequest): R {
        val userId = request.getHeader("userId")
        val time = request.getHeader("time")
        val token = request.getHeader("token")

        val email = request.getParam("email")

        //检查用户名是否注册，
        val inviter = UserEntity.findById(Convert.toLong(userId)) ?: return R.fail(WebCode.User_Not_Found)
        //检查邮箱是否注册
        val findByEmail = UserEntity.findByEmail(email)
        if (findByEmail != null) {
            return R.fail(WebCode.User_Email_Exist)
        }

        //发送邮件
        val code = RandomUtil.randomString(5)
        val mailClient = contextMap["mailClient"] as MailClient
        val format = StrUtil.format(inviteMsgTemplate, inviter.name, code)
//        val mailResultFuture = EmailEvent(
//            "registerEmailCode", "admin@vxph.com", ListUtil.toList(email), ListUtil.empty(),
//            "邀请注册邮件", format,
//            null
//        ).send(mailClient).onSuccess {
//            log.info("发送邀请成功. receiveEmail:{}   msg:{} ,  result:{}", email, format, it.toJson())
//            val inviteEntity = InviteEntity()
//            inviteEntity.code = code
//            inviteEntity.email = email
//            inviteEntity.sender = inviter.id
//            inviteEntity.expire = LocalDateTime.now().plusDays(7)
//            inviteEntity.createTime = LocalDateTime.now()
//            inviteEntity.status = 0
//            inviteEntity.updateTime = inviteEntity.createTime
//            inviteEntity.upsert()
//        }.onFailure {
//            log.error("发送邀请失败. receiveEmail:{}   msg:{}  result:{}", email, format, it.toJson())
//        }
        val result = awaitResult<MailResult> {
            EmailEvent(
                "registerEmailCode", "admin@vxph.com", ListUtil.toList(email), ListUtil.empty(),
                "邀请注册邮件", format,
                null
            ).send(mailClient).onComplete(it)
        }
        return R.success(result.toJson())
    }


    /**
     *
     */
    @Path("/user/signUp")
    fun signUp(request: HttpServerRequest): R {


        val nickName = request.getParam("nickName")
        val time = request.getParam("timestamp")
        val sign = request.getParam("sign")

        //判断是否超时
        if (System.currentTimeMillis() - time.toLong() > expireMinutes * 30 * 1000L) {
            return R.fail(WebCode.Login_Time_Expire)
        }

        val userEntity = UserEntity.findByName(nickName) ?: return R.fail(WebCode.User_Not_Found)
        val accountEntity = AccountEntity.findByUserId(userEntity.id!!)

        val signStr = "${userEntity.name}&${userEntity.password}&${time}"
        val sha256Hex = DigestUtil.sha256Hex(signStr)
        return if (sign == sha256Hex) {
            userEntity.password = ""
            val result = mapOf("time" to time, "sha256" to sha256Hex, "userId" to userEntity.id)
            R.success().add("userInfo", userEntity)
                .add("account", accountEntity!!)
                .add("token", result)
        } else {
            R.fail(WebCode.User_Name_Password_Error)
        }
    }

}