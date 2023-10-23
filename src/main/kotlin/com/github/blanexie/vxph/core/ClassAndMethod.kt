package com.github.blanexie.vxph.core

import java.lang.reflect.Method


data class ClassAndMethod(val clazz: Class<*>, val method: Method, val path: String, val reqMethod: String)


