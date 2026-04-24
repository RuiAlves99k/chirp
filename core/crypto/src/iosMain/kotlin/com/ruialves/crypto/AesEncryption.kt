@file:OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)

package com.ruialves.crypto

import co.touchlab.kermit.Logger
import com.ruialves.core.domain.util.Result
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UByteVar
import kotlinx.cinterop.ULongVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.CoreCrypto.CCCrypt
import platform.CoreCrypto.kCCAlgorithmAES
import platform.CoreCrypto.kCCBlockSizeAES128
import platform.CoreCrypto.kCCDecrypt
import platform.CoreCrypto.kCCEncrypt
import platform.CoreCrypto.kCCKeySizeAES256
import platform.CoreCrypto.kCCOptionPKCS7Padding
import platform.CoreFoundation.CFTypeRefVar
import platform.Foundation.CFBridgingRelease
import platform.Foundation.NSData
import platform.Foundation.create
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecRandomCopyBytes
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleWhenUnlockedThisDeviceOnly
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecRandomDefault
import platform.Security.kSecReturnData
import platform.Security.kSecValueData
import platform.Foundation.CFBridgingRetain
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFDictionarySetValue
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreFoundation.kCFBooleanTrue
import platform.CoreFoundation.kCFTypeDictionaryKeyCallBacks
import platform.CoreFoundation.kCFTypeDictionaryValueCallBacks
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFStringCreateWithCString
import platform.CoreFoundation.kCFStringEncodingUTF8
import platform.posix.memcpy

/**
 * iOS [Encryption] implementation using AES-256-CBC with PKCS7 padding via CommonCrypto.
 *
 * **Key management:** Encryption keys are 256-bit, generated with [SecRandomCopyBytes] and stored
 * in the iOS Keychain with [kSecAttrAccessibleWhenUnlockedThisDeviceOnly] — keys are inaccessible
 * when the device is locked and are excluded from backups. Each [keyAlias] maps to an independent
 * Keychain entry, allowing key isolation per feature.
 *
 * **Encryption format:** The output of [encrypt] is `IV (16 bytes) + ciphertext`. The IV is
 * randomly generated per operation via [SecRandomCopyBytes]. This matches the Android
 * [AesEncryption] output format, so encrypted data is structurally compatible across platforms
 * (though keys are platform-bound and not transferable).
 *
 * **Cinterop notes:** Keychain queries are built with [CFDictionaryCreateMutable] and
 * [CFDictionarySetValue] rather than Kotlin `Map` because the Security framework expects
 * raw CF-typed keys and values. String values use [CFStringCreateWithCString] to produce
 * proper [CFStringRef] pointers.
 */
object AesEncryption : Encryption {
    private val KEY_SIZE = kCCKeySizeAES256.toInt() // 32 bytes
    private val IV_SIZE = kCCBlockSizeAES128.toInt() // 16 bytes
    private const val SERVICE_NAME = "com.ruialves.chirp.crypto"
    private const val TAG = "AesEncryption"

    override fun encrypt(data: ByteArray, keyAlias: String): Result<ByteArray, EncryptionError> {
        return try {
            val key = getOrCreateKey(keyAlias)
                ?: return Result.Failure(EncryptionError.KEY_NOT_FOUND)

            val iv = generateSecureRandom(IV_SIZE)
                ?: return Result.Failure(EncryptionError.ENCRYPTION_FAILED)

            val encrypted = ccCrypt(kCCEncrypt, key, iv, data)
                ?: return Result.Failure(EncryptionError.ENCRYPTION_FAILED)

            Result.Success(iv + encrypted)
        } catch (e: Exception) {
            Logger.e(e, TAG) { "Encrypt failed" }
            Result.Failure(EncryptionError.ENCRYPTION_FAILED)
        }
    }

    override fun decrypt(data: ByteArray, keyAlias: String): Result<ByteArray, EncryptionError> {
        return try {
            if (data.size < IV_SIZE + 1) {
                return Result.Failure(EncryptionError.DECRYPTION_FAILED)
            }

            val key = getOrCreateKey(keyAlias)
                ?: return Result.Failure(EncryptionError.KEY_NOT_FOUND)

            val iv = data.copyOfRange(0, IV_SIZE)
            val ciphertext = data.copyOfRange(IV_SIZE, data.size)

            val decrypted = ccCrypt(kCCDecrypt, key, iv, ciphertext)
                ?: return Result.Failure(EncryptionError.DECRYPTION_FAILED)

            Result.Success(decrypted)
        } catch (e: Exception) {
            Logger.e(e, TAG) { "Decrypt failed" }
            Result.Failure(EncryptionError.DECRYPTION_FAILED)
        }
    }

    private fun ccCrypt(
        operation: UInt,
        key: ByteArray,
        iv: ByteArray,
        input: ByteArray,
    ): ByteArray? = memScoped {
        val outputSize = input.size + IV_SIZE
        val output = allocArray<UByteVar>(outputSize)
        val dataOutMoved = alloc<ULongVar>()

        val status = key.usePinned { keyPinned ->
            iv.usePinned { ivPinned ->
                input.usePinned { inputPinned ->
                    CCCrypt(
                        op = operation,
                        alg = kCCAlgorithmAES,
                        options = kCCOptionPKCS7Padding,
                        key = keyPinned.addressOf(0),
                        keyLength = key.size.toULong(),
                        iv = ivPinned.addressOf(0),
                        dataIn = inputPinned.addressOf(0),
                        dataInLength = input.size.toULong(),
                        dataOut = output,
                        dataOutAvailable = outputSize.toULong(),
                        dataOutMoved = dataOutMoved.ptr,
                    )
                }
            }
        }

        if (status == 0) {
            output.reinterpret<ByteVar>().readBytes(dataOutMoved.value.toInt())
        } else {
            null
        }
    }

    private fun getOrCreateKey(keyAlias: String): ByteArray? {
        return loadKeyFromKeychain(keyAlias) ?: generateAndStoreKey(keyAlias)
    }

    private fun loadKeyFromKeychain(keyAlias: String): ByteArray? = memScoped {
        val query = CFDictionaryCreateMutable(kCFAllocatorDefault, 5, kCFTypeDictionaryKeyCallBacks.ptr, kCFTypeDictionaryValueCallBacks.ptr)!!
        CFDictionarySetValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionarySetValue(query, kSecAttrService, SERVICE_NAME.toCFString())
        CFDictionarySetValue(query, kSecAttrAccount, keyAlias.toCFString())
        CFDictionarySetValue(query, kSecReturnData, kCFBooleanTrue)
        CFDictionarySetValue(query, kSecMatchLimit, kSecMatchLimitOne)

        val result = alloc<CFTypeRefVar>()
        val status = SecItemCopyMatching(query, result.ptr)

        if (status == errSecSuccess) {
            (CFBridgingRelease(result.value) as? NSData)?.toByteArray()
        } else {
            null
        }
    }

    private fun generateAndStoreKey(keyAlias: String): ByteArray? = memScoped {
        val key = generateSecureRandom(KEY_SIZE) ?: return null
        val keyData = key.toNSData()

        val query = CFDictionaryCreateMutable(kCFAllocatorDefault, 5, kCFTypeDictionaryKeyCallBacks.ptr, kCFTypeDictionaryValueCallBacks.ptr)!!
        CFDictionarySetValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionarySetValue(query, kSecAttrService, SERVICE_NAME.toCFString())
        CFDictionarySetValue(query, kSecAttrAccount, keyAlias.toCFString())
        CFDictionarySetValue(query, kSecAttrAccessible, kSecAttrAccessibleWhenUnlockedThisDeviceOnly)
        CFDictionarySetValue(query, kSecValueData, CFBridgingRetain(keyData))

        val status = SecItemAdd(query, null)
        if (status != errSecSuccess) return null
        key
    }

    private fun generateSecureRandom(size: Int): ByteArray? {
        val bytes = ByteArray(size)
        bytes.usePinned { pinned ->
            val status = SecRandomCopyBytes(kSecRandomDefault, size.toULong(), pinned.addressOf(0))
            if (status != errSecSuccess) return null
        }
        return bytes
    }
}

private fun String.toCFString(): CFStringRef {
    return CFStringCreateWithCString(kCFAllocatorDefault, this, kCFStringEncodingUTF8)!!
}

private fun ByteArray.toNSData(): NSData {
    return usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
    }
}

private fun NSData.toByteArray(): ByteArray {
    val length = length.toInt()
    if (length == 0) return ByteArray(0)
    val bytes = ByteArray(length)
    bytes.usePinned { pinned ->
        memcpy(pinned.addressOf(0), this@toByteArray.bytes, length.toULong())
    }
    return bytes
}
