package com.github.blanexie.vxph.torrent.controller

import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.date.DateUtil
import cn.hutool.core.img.ImgUtil
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
    private val fileResourceService: FileResourceService,
    @Value("\${vxph.data.dir}")
    private val filePath: String,
) {


    @ResponseBody
    @PostMapping("/upload")
    fun uploadFile(@RequestPart file: MultipartFile, @RequestParam hash: String): WebResp {
        val loginUserId = StpUtil.getLoginIdAsLong()
        val user = userService.findById(loginUserId)
        //获取后缀
        val suffix = FileUtil.getSuffix(file.originalFilename)
        val fileResource = FileResource(hash, file.originalFilename, suffix, file.size, user!!)
        //先保存文件，再保存数据库记录，保证数据库中的记录必定会有对应的文件
        file.transferTo(File("${filePath}/file/${fileResource.hash}.${fileResource.suffix}"))
        val resource = fileResourceService.saveFile(fileResource)
        return WebResp.ok(resource)
    }

    @GetMapping("/{hash}.{suffix}")
    fun download(@PathVariable("hash") hash: String, @PathVariable("suffix") suffix: String, response: HttpServletResponse) {
        val fileResource = fileResourceService.findByHash(hash)
        response.setHeader("Content-Type", "image/${fileResource.suffix}")
        response.setHeader("Cache-Control", "max-age=604800, public ")
        response.setHeader("Pragma", "public")
        val bufferedInputStream = BufferedOutputStream(response.outputStream)
        val file = File("$filePath/file/${fileResource.hash}.${fileResource.suffix}")
        file.inputStream().use {
            IoUtil.copy(it, bufferedInputStream)
        }
        bufferedInputStream.flush()
    }

    @GetMapping("/delete")
    fun delete(@RequestParam("hash") hash: String): WebResp {
        val fileResource = fileResourceService.deleteByHash(hash)
        File("$filePath/file/${fileResource.hash}.${fileResource.suffix}").deleteOnExit()
        return WebResp.ok()
    }

}