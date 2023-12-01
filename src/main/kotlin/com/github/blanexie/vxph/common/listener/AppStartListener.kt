package com.github.blanexie.vxph.common.listener

import cn.hutool.core.io.IoUtil
import cn.hutool.core.io.resource.ClassPathResource
import cn.hutool.core.util.StrUtil
import com.github.blanexie.vxph.user.dto.PermissionType
import com.github.blanexie.vxph.user.entity.Permission
import com.github.blanexie.vxph.user.repository.PermissionRepository
import com.github.blanexie.vxph.user.repository.RoleRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import org.springframework.web.util.pattern.PathPattern
import kotlin.jvm.optionals.getOrNull

@Component
class AppStartListener(
    private val requestMappingHandlerMapping: RequestMappingHandlerMapping,
    private val permissionRepository: PermissionRepository,
    private val roleRepository: RoleRepository,
    private val jdbcTemplate: JdbcTemplate,
) : ApplicationListener<ApplicationReadyEvent> {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        this.initTable()
        this.processPathPermission()
    }

    fun initTable() {
        //检查是否需要初始化
        val roleAdmin = roleRepository.findById(1L).getOrNull()
        if (roleAdmin != null) {
            log.info("初始化数据完成------------------")
            return
        }
        log.info("开始初始化数据")

        IoUtil.getReader(ClassPathResource("data.sql").stream, Charsets.UTF_8).lines()
            .map { StrUtil.trim(it) }
            .filter { StrUtil.isNotBlank(it) }
            .filter { !StrUtil.startWith(it, "--") }
            .forEach { sql ->
                try {
                    jdbcTemplate.execute(sql)
                    log.info("execute SQL: {}", sql)
                } catch (e: Exception) {
                    log.error("", e)
                }
            }
        log.info("初始化数据完成------------------")
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