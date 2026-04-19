package com.ruialves.core.data.di

import com.ruialves.core.data.auth.KtorAuthService
import com.ruialves.core.data.logging.KermitLogger
import com.ruialves.core.data.networking.HttpClientFactory
import com.ruialves.core.domain.auth.AuthService
import com.ruialves.core.domain.logging.ChirpLogger
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformCoreDataModule : Module

val coreDataModule = module {
    includes(platformCoreDataModule)
    single<ChirpLogger> { KermitLogger }
    single {
        HttpClientFactory(
            chirpLogger = get()
        ).create(get())
    }

    singleOf(::KtorAuthService) bind AuthService::class
}
