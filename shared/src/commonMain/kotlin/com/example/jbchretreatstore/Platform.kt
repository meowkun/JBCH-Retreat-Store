package com.example.jbchretreatstore

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform