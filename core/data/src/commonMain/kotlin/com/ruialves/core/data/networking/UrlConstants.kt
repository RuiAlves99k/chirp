package com.ruialves.core.data.networking

import com.ruialves.core.data.BuildKonfig

object UrlConstants {
    val BASE_URL_HTTP = "https://${BuildKonfig.BASE_URL}/api"
    val BASE_URL_WS = "wss://${BuildKonfig.BASE_URL}/ws"
}
