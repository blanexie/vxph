package com.github.blanexie.vxph.user.service

interface CodeService {

    fun findAnnounceUrl(): List<String>

    fun findValueByCode(code: String): String?

}