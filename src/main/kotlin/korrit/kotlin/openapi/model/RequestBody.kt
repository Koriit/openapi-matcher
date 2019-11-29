package korrit.kotlin.openapi.model

import korrit.kotlin.openapi.YAML_INDENT

/**
 * Representation of [Request Body Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#requestBodyObject).
 *
 * @WIP
 */
class RequestBody(
    val content: List<MediaType>,
    val required: Boolean
) {

    override fun toString(): String = StringBuilder().apply {
        if (required) {
            append("required: true")
            appendln()
        }
        append("content:")
        content.forEach {
            appendln()
            append(it.toString().prependIndent(YAML_INDENT))
        }
    }.toString()
}
