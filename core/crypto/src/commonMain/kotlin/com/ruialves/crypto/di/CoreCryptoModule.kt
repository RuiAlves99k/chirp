package com.ruialves.crypto.di

import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformCoreCryptoModule: Module

val coreCryptoModule = module {
    includes(platformCoreCryptoModule)
}
