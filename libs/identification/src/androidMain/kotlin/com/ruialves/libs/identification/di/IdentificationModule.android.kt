package com.ruialves.libs.identification.di

import com.ruialves.libs.identification.AndroidDeviceIdentification
import com.ruialves.libs.identification.DeviceIdentification
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val identificationModule = module {
    single<DeviceIdentification> { AndroidDeviceIdentification(context = androidContext()) }
}
