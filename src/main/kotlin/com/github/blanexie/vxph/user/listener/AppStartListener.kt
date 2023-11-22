package com.github.blanexie.vxph.user.listener

import com.github.blanexie.vxph.user.dto.PermissionType
import com.github.blanexie.vxph.user.entity.Permission
import com.github.blanexie.vxph.user.repository.PermissionRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import org.springframework.web.util.pattern.PathPattern

@Async
@Component
class AppStartListener(
    private val requestMappingHandlerMapping: RequestMappingHandlerMapping,
    private val permissionRepository: PermissionRepository,
) : ApplicationListener<ApplicationReadyEvent> {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
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

    fun findPathPermission(): List<Permission> {
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