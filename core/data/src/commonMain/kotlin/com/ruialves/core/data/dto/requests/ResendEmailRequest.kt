package com.ruialves.core.data.dto.requests

import kotlinx.serialization.Serializable

@Serializable
data class ResendEmailRequest(
    val email: String
)
