package com.github.blanexie.vxph.common.filter

import cn.hutool.cache.CacheUtil
import cn.hutool.core.codec.Base64
import cn.hutool.core.convert.Convert
import cn.hutool.core.text.AntPathMatcher
import cn.hutool.crypto.digest.DigestUtil
import com.github.blanexie.vxph.core.getProperty
import com.github.blanexie.vxph.core.objectMapper
import com.github.blanexie.vxph.core.web.Filter
import com.github.blanexie.vxph.core.web.HttpFilter
import com.github.blanexie.vxph.user.entity.AccountEntity
import com.github.blanexie.vxph.user.entity.CodeEntity
import com.github.blanexie.vxph.user.entity.UserEntity
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory

@Filter
class WebAuthFilter : HttpFilter {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val expireMinutes: Int = getProperty("vxph.http.token.expireMinutes", 15)

    private val tokenCache = CacheUtil.newLRUCache<String, Any>(1000, expireMinutes * 60 * 1000L)
    private val rolePathCache = CacheUtil.newLRUCache<String, Map<String, List<String>>>(1000, expireMinutes * 60 * 1000L)

    private val antPathMatcher = AntPathMatcher()
    private val RolePathCode = "role_path_manage"
    private val anonymous = "Anonymous"

    override fun before(ctx: RoutingContext): Boolean {
        val request = ctx.request()
        val header = request.getHeader("token") ?: anonymous
        val token = tokenCache.get("header_$header") {
            if (header != anonymous) {
                try {
                    val decode = Base64.decodeStr(header)
                    return@get objectMapper.readValue(decode, Map::class.java)
                } catch (e: Throwable) {
                    log.warn("token 解密失败：{} ", e.message)
                }
            }
            return@get mapOf("userId" to 0, "time" to System.currentTimeMillis(), "sha256" to anonymous)
        } as Map<*, *>

        val userId = Convert.toLong(token["userId"])
        val time = Convert.toLong(token["time"])
        val sha256 = Convert.toStr(token["sha256"])

        if (System.currentTimeMillis() - time > expireMinutes * 90 * 1000L) {
            //过期token， 返回错误
            return false
        }
        val role = findUserAccount(userId)
        //校验角色与路径的配置关系
        if (!checkRolePath(request.path(), role)) {
            return false
        }

        val sha256Hex = getUserSignature(userId, time)
        return if (sha256 == sha256Hex) {
            ctx.put("loginUserId", userId)
            true
        } else {
            false
        }
    }

    private fun getUserSignature(userId: Long, time: Long): String {
        if (userId == 0L) {
            return anonymous
        }
        val user = UserEntity.findById(userId) ?: return anonymous
        //验证签名
        val signature = "${user.id}&${user.password}&$time"
        val sha256Hex = tokenCache.get("signature_$signature") {
            DigestUtil.sha256Hex(signature)
        }
        log.info("signature:{}  sha256Hex:{}", signature, sha256Hex)
        return sha256Hex as String
    }

    private fun findUserAccount(userId: Long): String {
        if (userId == 0L) {
            return anonymous
        }
        val accountEntity = AccountEntity.findByUserId(userId) ?: return anonymous
        if (accountEntity.status != 0) {
            //用户的角色已经失效， 返回错误
            return anonymous
        }
        return accountEntity.role
    }

    private fun checkRolePath(path: String, role: String): Boolean {
        val rolePathsMap = rolePathCache.get(RolePathCode) {
            val findByCode = CodeEntity.findByCode(RolePathCode) ?: return@get mapOf()
            val content = findByCode.content
            val readValue = objectMapper.readValue(content, List::class.java)
            val map = hashMapOf<String, ArrayList<String>>()
            readValue.forEach { m ->
                val mm = m as Map<*, *>
                val key = mm["path"] as String
                val keys = map.computeIfAbsent(mm["role"] as String) {
                    arrayListOf()
                }
                keys.add(key)
            }
            map
        }

        val paths = rolePathsMap[role]
        if (paths == null) {
            //当前登录用户的角色没有配置路径， 则当前路径默认登录才能访问
            return true
        }
        for (key in paths) {
            if (antPathMatcher.match(key, path)) {
                return true
            }
        }
        //没有命中path,  直接返回错误信息，
        return false
    }


    override fun exception(ctx: RoutingContext, e: Throwable): Boolean {
        return true
    }
}
