package korrit.kotlin.openapi.model

import korrit.kotlin.openapi.YAML_INDENT

/**
 * Representation of [MediaType Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#mediaTypeObject).
 *
 * @WIP
 */
class MediaType(
    val contentType: String,
    val schema: Schema?
) {
    /**
     * Returns YAML representation.
     */
    override fun toString(): String {
        val spec = StringBuilder().apply {
            schema?.let {
                val schemaSpec = schema.toString()
                if (schemaSpec.isNotBlank()) {
                    appendln()
                    append("schema:")
                    append(schemaSpec.prependIndent(YAML_INDENT))
                }
            }
        }

        if (spec.isBlank()) {
            return "$contentType: {}"
        } else {
            return "$contentType:" + spec.toString().prependIndent(YAML_INDENT)
        }
    }
}
