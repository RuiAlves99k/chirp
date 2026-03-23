package com.ruialves.chirp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform