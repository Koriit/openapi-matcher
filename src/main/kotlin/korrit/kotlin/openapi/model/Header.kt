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
    override fun toString(): String = "$name:" + StringBuilder().apply {
        if (required) {
            appendln()
            append("required: true")
        }
        if (deprecated) {
            appendln()
            append("deprecated: true")
        }
        appendln()
        append("schema:")
        append(schema.toString().prependIndent(YAML_INDENT))
    }.toString().prependIndent(YAML_INDENT)
}
