package com.denis.domain.model

class NoResultsFound(override val message: String? = null): Throwable(message)
class NetworkError(val code: Int, override val message: String? = null): Throwable(message)