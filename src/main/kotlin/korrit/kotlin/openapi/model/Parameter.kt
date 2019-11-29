package korrit.kotlin.openapi.model

import korrit.kotlin.openapi.YAML_INDENT

/**
 * Representation of [Parameter Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#parameterObject).
 *
 * @WIP
 */
class Parameter(
    val name: String,
    val inside: String,
    val required: Boolean,
    val deprecated: Boolean,
    val description: String?,
    val schema: Schema
) {

    override fun toString(): String = StringBuilder().apply {
        appendln("name: $name")
        append("in: $inside")
        if (required) {
            appendln()
            append("required: true")
        }
        if (deprecated) {
            appendln()
            append("deprecated: true")
        }
        description?.let {
            appendln()
            append("description: $it")
        }
        appendln()
        appendln("schema:")
        append(schema.toString().prependIndent(YAML_INDENT))
    }.toString()
}
