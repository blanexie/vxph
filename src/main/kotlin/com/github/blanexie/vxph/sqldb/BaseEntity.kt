package com.github.blanexie.vxph.sqldb

import cn.hutool.core.util.StrUtil
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField


open class BaseEntity {

    init {
        //初始化基本的表
        val kClass = this::class
        val table = kClass.annotations.filterIsInstance<Table>().last()
        val name = if (table.name == "") {
            StrUtil.toUnderlineCase(kClass.simpleName)
        } else {
            table.name
        }
        var ddlSql = StringBuilder("create table $name ( ").appendLine()
        kClass.memberProperties.forEach {
            val columnSql = buildColumnSql(it)
            if (it.name == table.pk && table.pkAutoIncrement) {
                ddlSql.append(columnSql + " constraint ${name}_pk  primary key   autoincrement  ")
            } else {
                ddlSql.append(columnSql)
            }
            ddlSql.append(",").appendLine()
        }
        //主键的设置
        val pk = table.pk
        //多主键
        if (pk.contains(",") || !table.pkAutoIncrement) {
            ddlSql.append("  constraint ${name}_pk primary key (").append(pk).append(") ")
                .appendLine().append(" , ")
        }
        //单主键， 自增


        //唯一键
        if (table.uk != "") {
            ddlSql.append(" constraint ${name}_uk unique (").append(table.uk).append(") ")
                .append(" , ").appendLine()
        }

        //最后一个逗号的清除问题
        if (ddlSql.trim().endsWith(",")) {
            val index = ddlSql.lastIndexOf(",")
            ddlSql = ddlSql.replace(index, ddlSql.length, "").appendLine()
        }
        ddlSql.append("); ").appendLine()

        //索引
        if (table.ik != "") {
            ddlSql.append(" create index ${name}_index on ${name}(").append(table.ik).append(") ; ").appendLine()
                .appendLine()
        }

        println(ddlSql.toString())
    }


    private fun buildColumnSql(it: KProperty1<out BaseEntity, *>): String {
        val ddlSql = StringBuilder()
        val javaField = it.javaField!!
        ddlSql.append("  ").append(StrUtil.toUnderlineCase(it.name)).append(" ")
        if (Number::class.java.isAssignableFrom(javaField.type)) {
            if (javaField.type == Integer::class.java
                || javaField.type == Long::class.java
                || javaField.type == Short::class.java
            ) {
                ddlSql.append(" INTEGER  ")
            } else if (javaField.type == Float::class.java
                || javaField.type == Double::class.java
            ) {
                ddlSql.append(" NUMERIC  ")
            } else {
                ddlSql.append(" NUMERIC  ")
            }
        } else {
            ddlSql.append(" TEXT ")
        }
        return ddlSql.toString()
    }

}

@Table(
    name = "tt",
    ik = "c3",
    pk = "id,c1",
    pkAutoIncrement = true,
    uk = "c4"
)
class A(
    @PK(true)
    var id: Int?,
    var c1: Double?,
    var c2: Long?,
    var c3: String?,
    var c4: String?,
) : BaseEntity() {

}

fun main() {
    A(2, 2.4, 12L, "ss", "ss")
}