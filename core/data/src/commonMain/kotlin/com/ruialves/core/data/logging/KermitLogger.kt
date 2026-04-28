package com.ruialves.core.data.logging

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import com.ruialves.core.data.BuildKonfig
import com.ruialves.core.domain.crash.CrashReporter
import com.ruialves.core.domain.logging.ChirpLogger

class KermitLogger(
    private val crashReporter: CrashReporter,
) : ChirpLogger {

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
        crashReporter.addBreadcrumb(message, category = "warning")
    }

    override fun error(
        message: String,
        throwable: Throwable?,
    ) {
        Logger.e(message, throwable)
        if (throwable != null) {
            crashReporter.captureException(throwable)
        } else {
            crashReporter.captureMessage(message)
        }
    }
}
