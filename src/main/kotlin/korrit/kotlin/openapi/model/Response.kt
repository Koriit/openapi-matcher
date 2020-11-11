package korrit.kotlin.openapi.model

import korrit.kotlin.openapi.YAML_INDENT

/**
 * Describes a single response from an API Operation.
 *
 * Representation of [Response Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#responseObject).
 *
 * @property code HTTP response code.
 * @property content Descriptions of potential response payloads
 * @property description A short description of the response.
 * @property headers Response headers definitions.
 *
 * @WIP
 */
class Response(
    val code: String,
    val content: List<MediaType>?,
    val description: String,
    val headers: List<Header>?
) {
    /**
     * Returns YAML representation.
     */
    override fun toString(): String {
        val spec = StringBuilder().apply {
            appendLine()
            appendLine("description: >-")
            append(description.prependIndent(YAML_INDENT))
            headers?.let {
                appendLine()
                append("headers:")
                val out = StringBuilder()
                for (header in headers) {
                    out.apply {
                        appendLine()
                        append(header.toString())
                    }
                }
                append(out.toString().prependIndent(YAML_INDENT))
            }
            content?.let {
                appendLine()
                append("content:")
                for (media in content) {
                    appendLine()
                    append(media.toString().prependIndent(YAML_INDENT))
                }
            }
        }

        return "\"$code\":" + spec.toString().prependIndent(YAML_INDENT)
    }
}
