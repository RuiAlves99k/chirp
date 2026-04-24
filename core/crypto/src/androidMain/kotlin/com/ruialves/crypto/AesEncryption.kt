package com.ruialves.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.StrongBoxUnavailableException
import com.ruialves.core.domain.util.Result
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

/**
 * Android [Encryption] implementation using AES-256-CBC with PKCS7 padding via AndroidKeyStore.
 *
 * **Key management:** Encryption keys are hardware-backed, generated and stored inside the
 * AndroidKeyStore. Keys are created with [StrongBox][KeyGenParameterSpec.Builder.setIsStrongBoxBacked]
 * when available (dedicated secure chip), falling back to TEE (Trusted Execution Environment).
 * Keys never leave the secure hardware — all encrypt/decrypt operations happen inside the chip.
 * Each [keyAlias] maps to an independent KeyStore entry, allowing key isolation per feature.
 *
 * **Encryption format:** The output of [encrypt] is `IV (16 bytes) + ciphertext`. The IV is
 * randomly generated per operation by the [Cipher] (enforced via `setRandomizedEncryptionRequired`).
 * This matches the iOS [AesEncryption] output format.
 */
object AesEncryption : Encryption {
    private const val PROVIDER = "AndroidKeyStore"
    private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
    private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
    private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    private const val IV_SIZE = 16 // AES block size

    private fun getKeyStore(): KeyStore {
        return KeyStore.getInstance(PROVIDER).apply { load(null) }
    }

    private fun getKey(keyAlias: String): SecretKey {
        val keyStore = getKeyStore()
        val existingKey = keyStore.getEntry(keyAlias, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey(keyAlias)
    }

    private fun createKey(keyAlias: String): SecretKey {
        return try {
            generateKey(keyAlias, strongBox = true)
        } catch (_: StrongBoxUnavailableException) {
            generateKey(keyAlias, strongBox = false)
        }
    }

    private fun generateKey(keyAlias: String, strongBox: Boolean): SecretKey {
        val spec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(BLOCK_MODE)
            .setEncryptionPaddings(PADDING)
            .setRandomizedEncryptionRequired(true)
            .setUserAuthenticationRequired(false)
            .setIsStrongBoxBacked(strongBox)
            .build()

        return KeyGenerator.getInstance(ALGORITHM, PROVIDER)
            .apply { init(spec) }
            .generateKey()
    }

    override fun encrypt(data: ByteArray, keyAlias: String): Result<ByteArray, EncryptionError> {
        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getKey(keyAlias))
            val iv = cipher.iv
            val encrypted = cipher.doFinal(data)
            Result.Success(iv + encrypted)
        } catch (_: Exception) {
            Result.Failure(EncryptionError.ENCRYPTION_FAILED)
        }
    }

    override fun decrypt(data: ByteArray, keyAlias: String): Result<ByteArray, EncryptionError> {
        return try {
            if (data.size < IV_SIZE + 1) {
                return Result.Failure(EncryptionError.DECRYPTION_FAILED)
            }
            val iv = data.copyOfRange(0, IV_SIZE)
            val encryptedData = data.copyOfRange(IV_SIZE, data.size)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, getKey(keyAlias), IvParameterSpec(iv))
            Result.Success(cipher.doFinal(encryptedData))
        } catch (_: Exception) {
            Result.Failure(EncryptionError.DECRYPTION_FAILED)
        }
    }
}
