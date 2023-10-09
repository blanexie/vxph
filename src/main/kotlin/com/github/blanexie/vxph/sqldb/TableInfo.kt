package com.github.blanexie.vxph.sqldb

class TableInfo(
    val init: Boolean,
    val ddlSql: String,
    val insertOrUpdateSql: String,
    val deleteSql: String,
    val findByIdSql: String,
)


