package com.ruialves.crypto

import com.ruialves.core.domain.util.Result

interface Encryption {
    fun encrypt(data: ByteArray, keyAlias: String): Result<ByteArray, EncryptionError>
    fun decrypt(data: ByteArray, keyAlias: String): Result<ByteArray, EncryptionError>
}
