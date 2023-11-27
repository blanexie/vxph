package com.github.blanexie.vxph.user.listener

import com.github.blanexie.vxph.account.entity.Account
import com.github.blanexie.vxph.account.repository.AccountRepository
import com.github.blanexie.vxph.ddns.entity.DomainRecord
import com.github.blanexie.vxph.ddns.repositroy.DomainRecordRepository
import com.github.blanexie.vxph.user.dto.PermissionType
import com.github.blanexie.vxph.user.entity.Code
import com.github.blanexie.vxph.user.entity.Permission
import com.github.blanexie.vxph.user.entity.Role
import com.github.blanexie.vxph.user.entity.User
import com.github.blanexie.vxph.user.repository.CodeRepository
import com.github.blanexie.vxph.user.repository.PermissionRepository
import com.github.blanexie.vxph.user.repository.RoleRepository
import com.github.blanexie.vxph.user.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import org.springframework.web.util.pattern.PathPattern

@Component
class AppStartListener(
    private val requestMappingHandlerMapping: RequestMappingHandlerMapping,
    private val permissionRepository: PermissionRepository,
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val accountRepository: AccountRepository,
    private val codeRepository: CodeRepository,
    private val domainRecordRepository: DomainRecordRepository,
) : ApplicationListener<ApplicationReadyEvent> {

    private val log = LoggerFactory.getLogger(this::class.java)


    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        this.processPathPermission()
        this.initTable()
    }


    /**
     * 初始化表结构
     */
    fun initTable() {
        //初始化ROLE
        val permissions = permissionRepository.findAll()
        val role = Role(1, "超级管理员", "admin", "超级管理员", permissions.toList())
        val save = roleRepository.save(role)
        val anonymouslyRoleCodes = listOf(
            "GET /announce", "POST /api/user/register", "GET /api/user/logout", "GET /scrape", "POST /api/user/login"
        )
        val anonymouslyRoles = permissions.filter { it -> anonymouslyRoleCodes.contains(it.code) }.toList()
        val role2 = Role(2, "匿名用户", "anonymously", "匿名用户", anonymouslyRoles)
        roleRepository.save(role2)
        //初始化user
        val user = userRepository.save(User(1, "admin", "admin@vxph.com", "123456", 1, save))
        accountRepository.save(
            Account(1, 0, 0, 0, 0, 0, "1", 0, user)
        )
        //初始化code
        val code1 = Code(1, "announceUrl", "Announce_Url", "[\"http://192.168.1.5:8016/announce\"]")
        val code2 = Code(2, "允许上传的资源文件后缀", "File_Allow_Suffix", "[\"jpg\",\"png\",\"jpeg\"]")
        val code3 = Code(3, "发送邀请函的文本模板", "InviteMailTemplateCode", "{\"subject\": \"VXPH邀请函，邀请你注册\", \"content\": \"邀请人：{name} \\n  邀请码：{code}\"} ")
        codeRepository.saveAll(listOf(code1, code2, code3))
        //DDNS
        val domainRecord = DomainRecord(1, "1242142214521", "xiezc.top", "AAAA", "@", "2408:820c:8f1b:9f80:c7d3:b6c3:eb8a:fb4c", 600, "ubuntu pi")
        domainRecordRepository.save(domainRecord)
    }


    /**
     * 写入项目中的所有路径权限信息
     */
    fun processPathPermission() {
        val pathPermissions = findPathPermission()
        pathPermissions.forEach {
            val permission = permissionRepository.findByCodeAndType(it.code, it.type)
            if (permission == null) {
                permissionRepository.save(it)
                log.info("save path: {} ", it.code)
            } else {
                log.info("path exist: {} ", it.code)
            }
        }
    }

    private fun findPathPermission(): List<Permission> {
        val permissions = arrayListOf<Permission>()
        val handlerMethods = requestMappingHandlerMapping.handlerMethods
        handlerMethods.forEach { (t, _) ->
            val pathPatterns = t.pathPatternsCondition!!.patterns as Set<PathPattern>
            val methods = t.methodsCondition.methods as Set<RequestMethod>
            pathPatterns.forEach { path ->
                methods.forEach { m ->
                    val code = "${m.name} ${path.patternString}"
                    val p = Permission(code, code, "", PermissionType.Path)
                    permissions.add(p)
                }
            }
        }
        return permissions
    }


}