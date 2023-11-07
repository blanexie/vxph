package com.github.blanexie.vxph.user

import cn.hutool.core.collection.ListUtil
import cn.hutool.core.convert.Convert
import cn.hutool.core.util.RandomUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.crypto.digest.DigestUtil
import com.github.blanexie.vxph.core.Verticle
import com.github.blanexie.vxph.core.contextMap
import com.github.blanexie.vxph.core.getProperty
import com.github.blanexie.vxph.core.web.HttpVerticle
import com.github.blanexie.vxph.core.web.Path
import com.github.blanexie.vxph.core.web.R
import com.github.blanexie.vxph.core.web.WebCode
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


private val inviteMsgTemplate =
    """
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
    suspend fun login(request: HttpServerRequest): R {
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

        val email = request.getParam("email")
        //校验用户是否存在生效中的邀请
        var inviteEntityList = InviteEntity.findByEmail(email)
        inviteEntityList = inviteEntityList.filter { it.checkActive() }.toList()
        if (inviteEntityList.isNotEmpty()) {
            return R.fail(WebCode.Invite_User_Exist)
        }

        // 获取当前登录用户
        val user = UserEntity.findById(Convert.toLong(userId)) ?: return R.fail(WebCode.User_Not_Found)
        //检查邮箱是否注册
        val findByEmail = UserEntity.findByEmail(email)
        if (findByEmail != null) {
            return R.fail(WebCode.User_Email_Exist)
        }
        //生成随机邀请码
        val code = RandomUtil.randomString(5)
        //创键邀请记录
        val inviteEntity = InviteEntity()
        inviteEntity.email = email
        inviteEntity.sender = user.id
        inviteEntity.code = code
        inviteEntity.status = 0
        inviteEntity.updateTime = LocalDateTime.now()
        inviteEntity.createTime = LocalDateTime.now()
        inviteEntity.expire = LocalDateTime.now().plusMonths(1)
        inviteEntity.upsert()
        //发送邮件
        val mailClient = contextMap["mailClient"] as MailClient
        val format = StrUtil.format(inviteMsgTemplate, user.name, code)
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
     * 注册
     */
    @Path("/user/signUp")
    fun signUp(request: HttpServerRequest): R {
        //签名， 使用code 加email  对其签名
        val signature = request.getParam("signature")
        val nickName = request.getParam("nickName")
        val password = request.getParam("password")
        val repeatPassword = request.getParam("repeatPassword")
        val inviteCode = request.getParam("inviteCode")
        val sex = request.getParam("sex")
        //检查两个密码是否一致
        if (repeatPassword != password) {
            return R.fail(WebCode.Password_Repeat_Error)
        }
        //检查邀请码是否已经失效或者使用
        val inviteEntity = InviteEntity.findByCode(code = inviteCode)
        if (inviteEntity == null || !inviteEntity.checkActive()) {
            return R.fail(WebCode.Invite_Code_Error)
        }
        val email = inviteEntity.email
        //检查签名是否正确
        val listOf = listOf(nickName, password, password, repeatPassword, inviteCode, sex)
        val joinToString = listOf.sorted().joinToString(",")
        val sha256Hex = DigestUtil.sha256Hex("${joinToString}${inviteCode}${email}")
        if (sha256Hex != signature) {
            return R.fail(WebCode.Signature_Check_Error)
        }
        //检查邮件 和 用户名是否已经注册
        val findByName = UserEntity.findByName(nickName)
        if (findByName != null) {
            return R.fail(WebCode.User_Name_Exist)
        }
        val findByEmail = UserEntity.findByEmail(email)
        if (findByEmail != null) {
            return R.fail(WebCode.User_Email_Exist)
        }
        // 开始新增用户
        val userEntity = UserEntity()
        userEntity.name = nickName
        userEntity.sex = Convert.toInt(sex, 0)
        userEntity.password = password
        userEntity.email = email
        userEntity.inviteId = inviteEntity.sender!!
        userEntity.status = 0
        userEntity.createTime = LocalDateTime.now()
        userEntity.updateTime = LocalDateTime.now()
        userEntity.upsert()
        //修改邀请记录的状态
        inviteEntity.status = 1
        inviteEntity.acceptTime = LocalDateTime.now()
        inviteEntity.upsert()
        return R.success(userEntity.id!!)
    }

}