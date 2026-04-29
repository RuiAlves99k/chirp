package com.ruialves.core.data.logging

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import com.ruialves.core.data.BuildKonfig
import com.ruialves.core.domain.crash.CrashReporter
import com.ruialves.core.domain.logging.ChirpLogger

class KermitLoggerBackend(
    private val crashReporter: CrashReporter,
) : ChirpLogger.Backend {

    init {
        Logger.setMinSeverity(
            if (BuildKonfig.IS_DEBUG) Severity.Debug else Severity.Warn
        )
    }

    override fun log(level: ChirpLogger.Level, tag: String?, message: String, throwable: Throwable?) {
        val logTag = tag ?: DEFAULT_TAG

        when (level) {
            ChirpLogger.Level.DEBUG -> Logger.d(tag = logTag) { message }
            ChirpLogger.Level.INFO -> Logger.i(tag = logTag) { message }
            ChirpLogger.Level.WARN -> {
                Logger.w(tag = logTag) { message }
                crashReporter.addBreadcrumb(message, category = "warning")
            }
            ChirpLogger.Level.ERROR -> {
                Logger.e(tag = logTag, throwable = throwable) { message }
                if (throwable != null) {
                    crashReporter.captureException(throwable)
                } else {
                    crashReporter.captureMessage(message)
                }
            }
        }
    }

    companion object {
        private const val DEFAULT_TAG = "Chirp"
    }
}
