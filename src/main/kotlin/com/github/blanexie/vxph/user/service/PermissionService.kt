package com.github.blanexie.vxph.user.service

import com.github.blanexie.vxph.user.entity.Permission

interface PermissionService {

    fun findById(id: Long): Permission

}