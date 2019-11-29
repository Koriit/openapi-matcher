package korrit.kotlin.openapi.model

import korrit.kotlin.openapi.YAML_INDENT

/**
 * Pair of key and [Schema Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject).
 */
class Property(
    val name: String,
    val schema: Schema
) {

    override fun toString(): String = StringBuilder().apply {
        appendln("$name:")
        append(schema.toString().prependIndent(YAML_INDENT))
    }.toString()
}
