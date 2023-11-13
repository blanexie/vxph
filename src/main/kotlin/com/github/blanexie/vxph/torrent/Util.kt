package com.github.blanexie.vxph.torrent

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class Util {
}

//started, completed或stopped之一
const val Event_Start = "started"
const val Event_Completed = "completed"
const val Event_Stopped = "stopped"
const val Event_Empty = "empty"


val bencode= Bencode()

val objectMapper = jacksonObjectMapper()