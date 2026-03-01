package com.luisamsampaio.jiggie

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform