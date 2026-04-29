package com.ruialves.libs.identification

import android.content.Context
import androidx.core.content.edit
import java.util.UUID

class AndroidDeviceIdentification(
    context: Context,
) : DeviceIdentification {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun getDeviceId(): String {
        val existing = prefs.getString(KEY_DEVICE_ID, null)
        if (existing != null) return existing

        val newId = UUID.randomUUID().toString()
        prefs.edit { putString(KEY_DEVICE_ID, newId) }
        return newId
    }

    companion object {
        private const val PREFS_NAME = "chirp_device"
        private const val KEY_DEVICE_ID = "device_id"
    }
}
