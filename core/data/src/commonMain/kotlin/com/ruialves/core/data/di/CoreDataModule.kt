package com.ruialves.core.data.di

import com.ruialves.core.data.auth.DataStoreSessionStorage
import com.ruialves.core.data.auth.KtorAuthService
import com.ruialves.core.data.crash.SentryCrashReporter
import com.ruialves.core.data.logging.KermitLogger
import com.ruialves.core.data.networking.HttpClientFactory
import com.ruialves.core.domain.auth.AuthService
import com.ruialves.core.domain.auth.SessionStorage
import com.ruialves.core.domain.crash.CrashReporter
import com.ruialves.core.domain.logging.ChirpLogger
import com.ruialves.crypto.di.coreCryptoModule
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformCoreDataModule: Module

val coreDataModule = module {
    includes(platformCoreDataModule)
    includes(coreCryptoModule)
    single<CrashReporter> { SentryCrashReporter(dsn = get(named("sentryDsn"))) }
    single<ChirpLogger> { KermitLogger(crashReporter = get()) }
    single {
        HttpClientFactory(
            chirpLogger = get(),
            sessionStorage = get()
        ).create(get())
    }

    singleOf(::KtorAuthService) bind AuthService::class
    singleOf(::DataStoreSessionStorage) bind SessionStorage::class
}
