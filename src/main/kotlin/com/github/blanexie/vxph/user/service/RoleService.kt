package com.github.blanexie.vxph.user.service

import com.github.blanexie.vxph.user.entity.Role

interface RoleService {


    fun findByCode(code: String): Role?


}