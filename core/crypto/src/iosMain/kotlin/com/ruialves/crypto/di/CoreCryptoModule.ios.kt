package com.ruialves.crypto.di

import com.ruialves.crypto.AesEncryption
import com.ruialves.crypto.Encryption
import org.koin.dsl.module

actual val platformCoreCryptoModule = module {
    single<Encryption> { AesEncryption }
}
