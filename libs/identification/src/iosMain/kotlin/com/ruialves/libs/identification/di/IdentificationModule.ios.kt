package com.ruialves.libs.identification.di

import com.ruialves.libs.identification.DeviceIdentification
import com.ruialves.libs.identification.IosDeviceIdentification
import org.koin.dsl.module

actual val identificationModule = module {
    single<DeviceIdentification> { IosDeviceIdentification() }
}
