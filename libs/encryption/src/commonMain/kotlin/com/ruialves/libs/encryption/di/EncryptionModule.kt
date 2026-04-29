package com.ruialves.libs.encryption.di

import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformEncryptionModule: Module

val encryptionModule = module {
    includes(platformEncryptionModule)
}
