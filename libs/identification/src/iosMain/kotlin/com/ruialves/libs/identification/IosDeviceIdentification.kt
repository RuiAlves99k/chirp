package com.ruialves.libs.identification

import platform.Foundation.NSUserDefaults
import platform.Foundation.NSUUID

class IosDeviceIdentification : DeviceIdentification {

    override fun getDeviceId(): String {
        val existing = NSUserDefaults.standardUserDefaults.stringForKey(KEY_DEVICE_ID)
        if (existing != null) return existing

        val newId = NSUUID().UUIDString
        NSUserDefaults.standardUserDefaults.setObject(newId, forKey = KEY_DEVICE_ID)
        return newId
    }

    companion object {
        private const val KEY_DEVICE_ID = "chirp_device_id"
    }
}
