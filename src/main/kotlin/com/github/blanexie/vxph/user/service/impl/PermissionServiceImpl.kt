package com.github.blanexie.vxph.user.service.impl

import cn.hutool.core.convert.Convert
import cn.hutool.core.util.StrUtil
import com.github.blanexie.vxph.common.getBean
import com.github.blanexie.vxph.user.entity.Permission
import com.github.blanexie.vxph.user.repository.PermissionRepository
import com.github.blanexie.vxph.user.service.PermissionService
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class PermissionServiceImpl(
    private val permissionRepository: PermissionRepository,
) : PermissionService {

    override fun findByCode(code: String): Permission? {
        return permissionRepository.findByCode(code)
    }

    override fun find(searchKey: String?, pageRequest: PageRequest): Page<Permission> {
        return this.findBySearchKey(searchKey, pageRequest)
    }

    fun findBySearchKey(searchKey: String?, pageRequest: PageRequest): Page<Permission> {
        val entityManager = getBean(EntityManager::class.java)
        val fromHql = " from Permission "
        val countHql = " select count(*) "
        val whereHql = " where code like :searchKey or name like :searchKey "

        val query = if (StrUtil.isNotBlank(searchKey)) {
            entityManager.createQuery(fromHql + whereHql)
                .setParameter("searchKey", "%$searchKey%")
        } else {
            entityManager.createQuery(fromHql)
        }
        val queryCount = if (StrUtil.isNotBlank(searchKey)) {
            entityManager.createQuery(countHql + fromHql + whereHql)
                .setParameter("searchKey", "%$searchKey%")
        } else {
            entityManager.createQuery(countHql + fromHql)
        }
        query.firstResult = pageRequest.offset.toInt()
        query.maxResults = pageRequest.pageSize

        val singleResult = Convert.toLong(queryCount.singleResult)
        val resultList = query.resultList as List<Permission>

        return PageImpl(resultList, pageRequest, singleResult)
    }
}