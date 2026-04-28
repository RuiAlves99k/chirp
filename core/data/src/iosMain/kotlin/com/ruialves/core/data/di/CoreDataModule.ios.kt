package com.ruialves.core.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.ruialves.core.data.auth.createDataStore
import com.ruialves.core.data.BuildKonfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val platformCoreDataModule = module {
    single<HttpClientEngine> { Darwin.create() }
    single<DataStore<Preferences>> { createDataStore() }
    single<String>(named("sentryDsn")) { BuildKonfig.SENTRY_DSN_IOS }
}
