package com.ruialves.core.data.logging

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import com.ruialves.core.data.BuildKonfig
import com.ruialves.core.domain.logging.ChirpLogger

object KermitLogger : ChirpLogger {

    init {
        Logger.setMinSeverity(
            if (BuildKonfig.IS_DEBUG) Severity.Debug else Severity.Warn
        )
    }

    override fun debug(message: String) {
        Logger.d(message)
    }

    override fun info(message: String) {
        Logger.i(message)
    }

    override fun warn(message: String) {
        Logger.w(message)
    }

    override fun error(
        message: String,
        throwable: Throwable?,
    ) {
        Logger.e(message, throwable)
    }
}
