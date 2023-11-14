package com.github.blanexie.vxph.torrent

const val announceIntervalMinute = 10
const val peerActiveExpireMinute = announceIntervalMinute * 2

//started, completed或stopped之一
const val Event_Start = "started"
const val Event_Completed = "completed"
const val Event_Stopped = "stopped"
const val Event_Empty = "empty"

enum class IpType {
    IPV4, IPV6
}

