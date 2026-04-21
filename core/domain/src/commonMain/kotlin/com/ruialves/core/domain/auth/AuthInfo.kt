package com.ruialves.core.domain.auth


data class AuthInfo(
    val accessToken: String,
    val refreshToken: String,
    val user: User
)
