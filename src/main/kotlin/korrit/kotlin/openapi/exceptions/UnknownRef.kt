package korrit.kotlin.openapi.exceptions

/**
 * Thrown when reference cannot bo resolved.
 */
class UnknownRef(ref: String) : RuntimeException("Cannot resolve reference: $ref")
