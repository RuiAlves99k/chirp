package com.ruialves.core.data.crash

import com.ruialves.core.data.BuildKonfig
import com.ruialves.core.domain.crash.CrashReporter
import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.SentryLevel
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb

class SentryCrashReporter(
    private val dsn: String,
) : CrashReporter {

    override fun initialize() {
        if (dsn.isBlank()) return

        Sentry.init { options ->
            options.dsn = dsn
            options.environment = BuildKonfig.FLAVOR_NAME
            options.tracesSampleRate = 0.2
        }
    }

    override fun captureException(throwable: Throwable) {
        Sentry.captureException(throwable)
    }

    override fun captureMessage(message: String) {
        Sentry.captureMessage(message)
    }

    override fun addBreadcrumb(message: String, category: String?) {
        val breadcrumb = Breadcrumb().apply {
            this.message = message
            this.category = category
            this.level = SentryLevel.WARNING
        }
        Sentry.addBreadcrumb(breadcrumb)
    }
}
