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
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedOutputStream
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.TimeZone

@RequestMapping("/api/resource")
@Controller
class FileResourceController(
    private val userService: UserService,
    private val fileResourceService: FileResourceService,
) {

    val dateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z")
    val streamProgress = StreamProgressImpl()

    @ResponseBody
    @PostMapping("/upload")
    fun uploadFile(@RequestPart file: MultipartFile, @RequestParam hash: String): WebResp {
        val loginUserId = StpUtil.getLoginIdAsLong()
        val user = userService.findById(loginUserId)
        //保存临时文件
        val tempFile = FileUtil.createTempFile()
        file.transferTo(tempFile)
        //获取后缀
        val suffix = FileUtil.getSuffix(file.originalFilename)
        val fileResource = FileResource(hash, file.originalFilename, suffix, tempFile.length(), user!!)
        val resource = fileResourceService.saveFile(fileResource, tempFile)
        return WebResp.ok(resource)
    }

    @GetMapping("/{hash}.{suffix}")
    fun download(@PathVariable("hash") hash: String, @PathVariable("suffix") suffix: String, response: HttpServletResponse) {
        val fileResourceDTO = fileResourceService.findFile(hash)
        val fileResource = fileResourceDTO.fileResource
        response.setHeader("Content-Type", "image/${fileResource.suffix}")
        response.setHeader("Cache-Control", "max-age=604800, public ")
        response.setHeader("Pragma", "public")

        val bufferedInputStream = BufferedOutputStream(response.outputStream)
        IoUtil.copyByNIO(fileResourceDTO.file.inputStream(), bufferedInputStream, 1024 * 1024 * 1024, streamProgress)
    }

    @GetMapping("/delete")
    fun delete(@RequestParam("hash") hash: String): WebResp {
        fileResourceService.deleteByHash(hash)
        return WebResp.ok()
    }

}