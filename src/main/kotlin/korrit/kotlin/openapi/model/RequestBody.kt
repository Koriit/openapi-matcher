package korrit.kotlin.openapi.model

import korrit.kotlin.openapi.YAML_INDENT

/**
 * Describes a single request body.
 *
 * Representation of [Request Body Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#requestBodyObject).
 *
 * @property content The content of the request body.
 * @property required Determines if the request body is required in the request.
 *
 * @WIP
 */
class RequestBody(
    val content: List<MediaType>,
    val required: Boolean
) {
    /**
     * Returns YAML representation.
     */
    override fun toString(): String = StringBuilder().apply {
        if (required) {
            append("required: true")
            appendln()
        }
        append("content:")
        for (media in content) {
            appendln()
            append(media.toString().prependIndent(YAML_INDENT))
        }
    }.toString()
}
