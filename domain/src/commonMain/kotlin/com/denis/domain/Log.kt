package com.denis.domain

enum class LogLevel { DEBUG, INFO, WARNING, ERROR }

object Log {
    var logLevel = LogLevel.DEBUG

    fun d(tag: String = "", message: String?) {
        log(tag, LogLevel.DEBUG, message, null)
    }

    fun i(tag: String = "", message: String?) {
        log(tag, LogLevel.INFO, message, null)
    }

    fun w(tag: String = "", message: String?) {
        log(tag, LogLevel.WARNING, message, null)
    }

    fun e(tag: String = "", message: String?, throwable: Throwable? = null) {
        log(tag, LogLevel.ERROR, message, throwable)
    }

    private fun log(tag: String = "", level: LogLevel, message: String?, throwable: Throwable?) {
        if (logLevel > level) return
        if (message.isNullOrEmpty() && (throwable == null || throwable.message.isNullOrEmpty())) return

        val logMessage = "[$tag] $message ${throwable?.message.orEmpty()}"
        println(logMessage)
    }
}