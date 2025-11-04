package com.example.jbchretreatstore

class JvmPlatform : Platform {
    override val name: String = ""
}

actual fun getPlatform(): Platform = JvmPlatform()