package com.ruialves.core.domain.crash

interface CrashReporter {
    fun initialize()

    fun captureException(throwable: Throwable)

    fun captureMessage(message: String)

    fun addBreadcrumb(message: String, category: String? = null)
}
