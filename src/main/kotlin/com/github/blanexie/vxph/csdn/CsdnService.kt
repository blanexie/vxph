package com.github.blanexie.vxph.csdn

import cn.hutool.core.thread.ThreadUtil
import cn.hutool.core.util.RandomUtil
import com.github.blanexie.vxph.common.objectMapper
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers

@Service
class CsdnService {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val httpClient = HttpClient.newBuilder().build()

    private var referer = "https://blog.csdn.net"

    @Scheduled(cron = "12 45 0/8 * * ? ")
    fun schedule() {
        log.info("博客访问程序启动")
        var page = 1
        var totalPage = 1
        //1. 访问博客
        reqDoc(referer)
        //2. 访问我的个人中心
        reqDoc("https://blog.csdn.net/leisurelen?type=blog")
        //3. 获取文章列表
        do {
            try {
                val url =
                    "https://blog.csdn.net/community/home-api/v1/get-business-list?page=${page}&size=20&businessType=blog&orderby=&noMore=false&year=&month=&username=leisurelen"
                val respJson = reqJson(url)
                val respMap = objectMapper.readValue(respJson, Map::class.java)
                //4. 解析出文章列表
                if (respMap["code"] == 200) {
                    val data = respMap["data"] as Map<String, Any>
                    val total = data["total"] as Int
                    totalPage = Math.ceil(total / 20.0).toInt()
                    val list = data["list"] as List<Map<String, Any>>
                    list.forEach {
                        reqDoc(it["url"] as String)
                    }
                }
            } catch (e: Exception) {
                log.error("循环中有报错", e)
            } finally {
                page++
            }
        } while (page <= totalPage)
        log.info("博客访问程序结束")
    }


    private fun reqDoc(url: String): String {
        try {
            val httpRequest =
                HttpRequest.newBuilder().uri(URI(url)).header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36 Edg/119.0.0.0").header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7").header("Referer", referer).build()
            val httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString())
            referer = url
            val ms = RandomUtil.randomDouble(1.0, 10.0) * 1000
            ThreadUtil.safeSleep(ms.toLong())
            log.info("request url:{},  resp:{}", url, httpResponse.statusCode())
            return httpResponse.body()
        } catch (e: Exception) {
            log.error("请求报错， url:{}", url, e)
            return ""
        }
    }

    private fun reqJson(url: String): String {
        try {
            val httpRequest =
                HttpRequest.newBuilder().uri(URI(url)).header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36 Edg/119.0.0.0").header("Accept", "application/json, text/plain, */*").header("Referer", referer).build()
            val httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString())
            referer = url
            val body = httpResponse.body()
            val ms = RandomUtil.randomDouble(1.0, 10.0) * 1000
            ThreadUtil.safeSleep(ms.toLong())
            log.info("request url:{},  resp:{}", url, httpResponse.statusCode())
            return body
        } catch (e: Exception) {
            log.error("请求报错， url:{}", url, e)
            return ""
        }
    }
}
