package com.korrit.kotlin.openapi.exceptions

/**
 * Generic exception of OpenAPI reading.
 */
open class ReaderException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
