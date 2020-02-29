package korrit.kotlin.openapi.model

import korrit.kotlin.openapi.YAML_INDENT

/**
 * Representation of [Header Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#headerObject).
 *
 * @WIP
 */
class Header(
    val name: String,
    val required: Boolean,
    val deprecated: Boolean,
    val schema: Schema
) {
    /**
     * Returns YAML representation.
     */
    override fun toString(): String {
        val spec = StringBuilder().apply {
            if (required) {
                appendln()
                append("required: true")
            }
            if (deprecated) {
                appendln()
                append("deprecated: true")
            }
            val schemaSpec = schema.toString()
            if (schemaSpec.isNotBlank()) {
                appendln()
                append("schema:")
                append(schemaSpec.prependIndent(YAML_INDENT))
            }
        }

        if (spec.isBlank()) {
            return "$name: {}"
        } else {
            return "$name:" + spec.toString().prependIndent(YAML_INDENT)
        }
    }
}
