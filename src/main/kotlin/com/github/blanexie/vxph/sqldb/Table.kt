package com.github.blanexie.vxph.sqldb

annotation class Table(
    val name: String = "",
    val pk: String,
    val pkAutoIncrement: Boolean,
    val uk: String = "",
    val ik: String = ""
)
