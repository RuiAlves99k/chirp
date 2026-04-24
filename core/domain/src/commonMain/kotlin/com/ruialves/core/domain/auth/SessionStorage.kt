package com.ruialves.core.domain.auth

import com.ruialves.core.domain.util.DataError
import com.ruialves.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow

interface SessionStorage {
    fun observeAuthInfo(): Flow<AuthInfo?>
    suspend fun set(info: AuthInfo?): EmptyResult<DataError.Local>
}
