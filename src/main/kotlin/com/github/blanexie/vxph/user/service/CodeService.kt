package com.github.blanexie.vxph.user.service

import cn.hutool.core.convert.Convert
import cn.hutool.core.util.StrUtil
import com.github.blanexie.vxph.common.objectMapper
import com.github.blanexie.vxph.torrent.Announce_Url_Code
import com.github.blanexie.vxph.user.repository.CodeRepository
import org.springframework.stereotype.Service

interface CodeService {


    fun findAnnounceUrl(): List<String>

    fun findValueByCode(code: String): String?


}