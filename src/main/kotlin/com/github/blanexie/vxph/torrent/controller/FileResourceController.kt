package com.github.blanexie.vxph.torrent.controller

import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.date.DateUtil
import cn.hutool.core.io.FileUtil
import cn.hutool.core.io.IoUtil
import com.github.blanexie.vxph.common.web.StreamProgressImpl
import com.github.blanexie.vxph.common.web.WebResp
import com.github.blanexie.vxph.torrent.entity.FileResource
import com.github.blanexie.vxph.torrent.service.FileResourceService
import com.github.blanexie.vxph.user.service.UserService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedOutputStream
import java.io.File
import java.io.InputStream
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.TimeZone

@RequestMapping("/api/resource")
@Controller
class FileResourceController(
    private val userService: UserService,
    @Value("\${vxph.file.path}")
    private val filePath: String,
    private val fileResourceService: FileResourceService,
) {


    @ResponseBody
    @PostMapping("/upload")
    fun uploadFile(@RequestPart file: MultipartFile, @RequestParam hash: String): WebResp {
        val loginUserId = StpUtil.getLoginIdAsLong()
        val user = userService.findById(loginUserId)
        //获取后缀
        val suffix = FileUtil.getSuffix(file.originalFilename)
        val fileResource = FileResource(hash, file.originalFilename, suffix, file.size, user!!)
        val resource = fileResourceService.saveFile(fileResource)
        file.transferTo(File("${filePath}/${fileResource.hash}.${fileResource.suffix}"))
        return WebResp.ok(resource)
    }

    @GetMapping("/{hash}.{suffix}")
    fun download(@PathVariable("hash") hash: String, @PathVariable("suffix") suffix: String, response: HttpServletResponse) {
        val fileResource = fileResourceService.findByHash(hash)
        response.setHeader("Content-Type", "image/${fileResource.suffix}")
        response.setHeader("Cache-Control", "max-age=604800, public ")
        response.setHeader("Pragma", "public")
        val bufferedInputStream = BufferedOutputStream(response.outputStream)
        val file = File("$filePath/${fileResource.hash}.${fileResource.suffix}")
        file.inputStream().use {
            IoUtil.copy(it, bufferedInputStream)
        }
    }

    @GetMapping("/delete")
    fun delete(@RequestParam("hash") hash: String): WebResp {
        fileResourceService.deleteByHash(hash)
        return WebResp.ok()
    }

}