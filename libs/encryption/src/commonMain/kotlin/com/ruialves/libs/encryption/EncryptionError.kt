package com.ruialves.libs.encryption

import com.ruialves.core.domain.util.Error

enum class EncryptionError : Error {
    KEY_NOT_FOUND,
    ENCRYPTION_FAILED,
    DECRYPTION_FAILED,
}
