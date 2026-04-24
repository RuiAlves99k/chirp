package com.ruialves.core.data.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ruialves.core.data.dto.AuthInfoSerializable
import com.ruialves.core.data.mappers.toDomain
import com.ruialves.core.data.mappers.toSerializable
import com.ruialves.core.domain.auth.AuthInfo
import com.ruialves.core.domain.auth.SessionStorage
import com.ruialves.core.domain.logging.ChirpLogger
import com.ruialves.core.domain.util.DataError
import com.ruialves.core.domain.util.EmptyResult
import com.ruialves.core.domain.util.Result
import com.ruialves.crypto.Encryption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
class DataStoreSessionStorage(
    private val dataStore: DataStore<Preferences>,
    private val encryption: Encryption,
    private val logger: ChirpLogger,
) : SessionStorage {

    private val authInfoKey = stringPreferencesKey("KEY_AUTH_INFO")
    private val json = Json {
        ignoreUnknownKeys = true
    }

    companion object {
        private const val KEY_ALIAS = "chirp_auth"
    }

    override fun observeAuthInfo(): Flow<AuthInfo?> {
        return dataStore.data.map { preferences ->
            val encryptedBase64 = preferences[authInfoKey] ?: return@map null

            val encryptedBytes = try {
                Base64.decode(encryptedBase64)
            } catch (e: IllegalArgumentException) {
                logger.error("Failed to decode Base64 auth info", e)
                return@map null
            }

            when (val result = encryption.decrypt(encryptedBytes, KEY_ALIAS)) {
                is Result.Success -> {
                    try {
                        val jsonString = result.data.decodeToString()
                        json.decodeFromString<AuthInfoSerializable>(jsonString).toDomain()
                    } catch (e: Exception) {
                        logger.error("Failed to deserialize auth info", e)
                        null
                    }
                }
                is Result.Failure -> {
                    logger.error("Failed to decrypt auth info: ${result.error}")
                    null
                }
            }
        }
    }

    override suspend fun set(info: AuthInfo?): EmptyResult<DataError.Local> {
        if (info == null) {
            dataStore.edit { it.remove(authInfoKey) }
            return Result.Success(Unit)
        }

        val jsonString = json.encodeToString(info.toSerializable())
        val jsonBytes = jsonString.encodeToByteArray()

        return when (val result = encryption.encrypt(jsonBytes, KEY_ALIAS)) {
            is Result.Success -> {
                val encryptedBase64 = Base64.encode(result.data)
                dataStore.edit { prefs ->
                    prefs[authInfoKey] = encryptedBase64
                }
                Result.Success(Unit)
            }
            is Result.Failure -> {
                logger.error("Failed to encrypt auth info: ${result.error}")
                Result.Failure(DataError.Local.UNKNOWN)
            }
        }
    }
}
