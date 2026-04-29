package com.ruialves.core.domain.logging

object ChirpLogger {
    private var backend: Backend? = null

    fun install(backend: Backend) {
        this.backend = backend
    }

    fun d(message: () -> String) { backend?.log(Level.DEBUG, null, message(), null) }
    fun i(message: () -> String) { backend?.log(Level.INFO, null, message(), null) }
    fun w(message: () -> String) { backend?.log(Level.WARN, null, message(), null) }
    fun e(throwable: Throwable? = null, message: () -> String) { backend?.log(Level.ERROR, null, message(), throwable) }

    operator fun invoke(tag: String) = Tagged(tag)

    class Tagged(private val tag: String) {
        fun d(message: () -> String) { backend?.log(Level.DEBUG, tag, message(), null) }
        fun i(message: () -> String) { backend?.log(Level.INFO, tag, message(), null) }
        fun w(message: () -> String) { backend?.log(Level.WARN, tag, message(), null) }
        fun e(throwable: Throwable? = null, message: () -> String) { backend?.log(Level.ERROR, tag, message(), throwable) }
    }

    enum class Level { DEBUG, INFO, WARN, ERROR }

    interface Backend {
        fun log(level: Level, tag: String?, message: String, throwable: Throwable?)
    }
}
