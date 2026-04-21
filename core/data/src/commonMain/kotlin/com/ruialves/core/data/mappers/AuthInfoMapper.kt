package com.ruialves.core.data.mappers

import com.ruialves.core.data.dto.AuthInfoSerializable
import com.ruialves.core.data.dto.UserSerializable
import com.ruialves.core.domain.auth.AuthInfo
import com.ruialves.core.domain.auth.User

fun AuthInfoSerializable.toDomain(): AuthInfo = AuthInfo(
    accessToken = accessToken,
    refreshToken = refreshToken,
    user = user.toDomain(),
)

fun UserSerializable.toDomain(): User = User(
    id = id,
    email = email,
    username = username,
    hasVerifiedEmail = hasVerifiedEmail,
    profilePictureUrl = profilePictureUrl
)
