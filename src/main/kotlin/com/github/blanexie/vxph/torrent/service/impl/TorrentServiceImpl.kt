package com.github.blanexie.vxph.torrent.service.impl

import cn.hutool.core.convert.Convert
import cn.hutool.core.io.FileUtil
import cn.hutool.crypto.digest.DigestUtil
import com.github.blanexie.vxph.common.bencode
import com.github.blanexie.vxph.common.exception.SysCode
import com.github.blanexie.vxph.common.exception.VxphException
import com.github.blanexie.vxph.common.objectMapper
import com.github.blanexie.vxph.torrent.controller.dto.ScrapeData
import com.github.blanexie.vxph.torrent.controller.dto.ScrapeResp
import com.github.blanexie.vxph.torrent.entity.Peer
import com.github.blanexie.vxph.torrent.entity.Post
import com.github.blanexie.vxph.torrent.entity.Torrent
import com.github.blanexie.vxph.torrent.repository.TorrentRepository
import com.github.blanexie.vxph.torrent.service.TorrentService
import com.github.blanexie.vxph.torrent.util.readString
import com.github.blanexie.vxph.user.entity.User
import com.github.blanexie.vxph.user.service.CodeService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStream

@Service
class TorrentServiceImpl(
    private val torrentRepository: TorrentRepository,
    private val codeService: CodeService,
    @Value("\${vxph.data.dir}")
    val torrentPath: String,
) : TorrentService {


    override fun findAllByInfoHashIn(infoHash: List<String>): List<Torrent> {
        if (infoHash.isEmpty()) {
            return emptyList()
        }
        return torrentRepository.findAllByInfoHashIn(infoHash)
    }

    override fun findByInfoHash(infoHash: String): Torrent? {
        return torrentRepository.findByInfoHash(infoHash)
    }

    override fun writeTorrentBytes(peer: Peer, torrent: Torrent, outputStream: OutputStream) {
        val torrentMap = hashMapOf<String, Any>()
        torrentMap["comment"] = torrent.comment
        torrentMap["create date"] = torrent.creationDate
        torrentMap["create by"] = torrent.createdBy
        val announceUrl = codeService.findAnnounceUrl()
        if (announceUrl.isNotEmpty() && announceUrl.size == 1) {
            torrentMap["announce"] = "${announceUrl[0]}?passkey=${peer.passKey}"
        } else {
            torrentMap["announce"] = "${announceUrl[0]}?passkey=${peer.passKey}"
            torrentMap["announce-list"] = announceUrl.map { "${it}?passkey=${peer.passKey}" }.toList()
        }
        val torrentBytes = bencode.encode(torrentMap)
        val infoBytes = File("${torrentPath}/torrent/${torrent.infoHash}").readBytes()
        outputStream.write(torrentBytes, 0, torrentBytes.size - 1)
        outputStream.write(byteArrayOf(0x34, 0x3a, 0x69, 0x6e, 0x66, 0x6f))
        outputStream.write(infoBytes)
        outputStream.write(0x65)
    }

    override fun saveTorrent(torrentMap: Map<String, Any>, user: User, post: Post, title: String): Torrent {
        //2. 解析文件内容
        val info = getPrivateInfo(torrentMap)
        val infoBytes = bencode.encode(info)
        val infoHash = DigestUtil.sha1Hex(infoBytes)
        val torrentExist = torrentRepository.findByInfoHash(infoHash)
        if (torrentExist != null) {
            return torrentExist
        }

        val pieceLength = Convert.toLong(info["piece length"])
        val length = getLength(info)
        val name = info.readString("name")
        val comment = info.readString("comment")
        val fileStr = getFileStr(info)
        val createDate = Convert.toLong(torrentMap["creation date"], 0)
        val createBy = info.readString("creation by")
        val single = info.containsKey("files")
        val torrent = Torrent(
            infoHash, title, name, length, comment, fileStr, createDate, createBy, pieceLength, single, 0,
            0, 0, arrayListOf(), user, post
        )
        //3. 保存文件和修改数据库
        FileUtil.writeBytes(infoBytes, File("${torrentPath}/torrent/${infoHash}"))
        return torrentRepository.save(torrent)
    }

    private fun getPrivateInfo(torrentMap: Map<String, Any>): HashMap<String, Any> {
        val info = torrentMap["info"] as HashMap<String, Any>
        info["private"] = 1
        return info
    }

    private fun getFileStr(info: HashMap<String, Any>): String {
        val result = arrayListOf<Map<String, Any>>()
        if (info.containsKey("files")) {
            val files = info["files"] as List<HashMap<String, Any>>
            files.forEach {
                it["name"] = it.readString("name")
            }
            result.addAll(files)
        } else {
            info.remove("pieces")
            info["name"] = info.readString("name")
            result.add(info)
        }
        return objectMapper.writeValueAsString(result)
    }

    private fun getLength(info: MutableMap<String, Any>): Long {
        return if (info.containsKey("files")) {
            //多文件
            val files = info["files"] as List<Map<String, Any>>
            files.sumOf { Convert.toLong(it["length"]) }
        } else {
            //单文件
            Convert.toLong(info["length"])
        }
    }

    /**
     * 批量获取
     */
    override fun processScrape(infoHash: List<String>): ScrapeResp {
        val torrents = torrentRepository.findAllByInfoHashIn(infoHash)
        val scrapeDataMap = hashMapOf<String, ScrapeData>()
        torrents.forEach {
            scrapeDataMap[it.infoHash] = ScrapeData(it.complete, it.incomplete, it.downloaded)
        }
        return ScrapeResp(scrapeDataMap)
    }

    override fun updateData(incomplete: Int, complete: Int, downloaded: Int, infoHash: String) {
        torrentRepository.updateData(incomplete, complete, downloaded, infoHash)
    }

}