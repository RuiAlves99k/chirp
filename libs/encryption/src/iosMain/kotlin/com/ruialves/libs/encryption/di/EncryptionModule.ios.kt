package com.ruialves.libs.encryption.di

import com.ruialves.libs.encryption.AesEncryption
import com.ruialves.libs.encryption.Encryption
import org.koin.dsl.module

actual val platformEncryptionModule = module {
    single<Encryption> { AesEncryption }
}
