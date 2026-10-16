package com.denis.domain

abstract class BaseUseCase {
    private var _value: Result<Any> = Result.success(Unit)
    private var requestStop: Boolean = false

    fun clear() {
        _value = Result.success(Unit)
        requestStop = false
    }
    private fun requestStop() {
        requestStop = true
    }
    protected fun <T>lastResult() = _value as Result<T>
    protected fun save(value: Result<Any>) { _value = value }

    protected suspend fun <T: BaseUseCase>execute(block: suspend () -> Unit): T {
        if (requestStop) {
            return this as T
        } else {
            block()
        }
        return this as T
    }

    protected fun <T: BaseUseCase>stopOnError(): T {
        val result = _value
        if (result.isFailure)
            requestStop()
        return this as T
    }
}