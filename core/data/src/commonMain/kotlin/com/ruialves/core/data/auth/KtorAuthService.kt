package com.ruialves.core.data.auth

import com.ruialves.core.data.dto.requests.RegisterRequest
import com.ruialves.core.data.dto.requests.ResendEmailRequest
import com.ruialves.core.data.networking.get
import com.ruialves.core.data.networking.post
import com.ruialves.core.domain.auth.AuthService
import com.ruialves.core.domain.util.DataError
import com.ruialves.core.domain.util.EmptyResult
import io.ktor.client.HttpClient

class KtorAuthService(
    private val httpClient: HttpClient
) : AuthService {

    override suspend fun register(
        email: String,
        username: String,
        password: String
    ): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "/auth/register",
            body = RegisterRequest(
                email = email,
                username = username,
                password = password
            )
        )
    }

    override suspend fun resendVerificationEmail(email: String): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = "/auth/resend-verification",
            body = ResendEmailRequest(
                email = email
            )
        )
    }

    override suspend fun verifyEmail(token: String): EmptyResult<DataError.Remote> {
        return httpClient.get(
            route = "/auth/verify",
            queryParams = mapOf("token" to token)
        )
    }

}
