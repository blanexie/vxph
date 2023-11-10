package com.github.blanexie.vxph.ddns.util

import java.net.http.HttpClient

val httpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build()