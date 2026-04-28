package com.ruialves.core.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.ruialves.core.data.auth.createDataStore
import com.ruialves.core.data.BuildKonfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val platformCoreDataModule = module {
    single<HttpClientEngine> {
        OkHttp.create()
    }
    single<DataStore<Preferences>> {
        createDataStore(androidContext())
    }
    single<String>(named("sentryDsn")) { BuildKonfig.SENTRY_DSN_ANDROID }
}
