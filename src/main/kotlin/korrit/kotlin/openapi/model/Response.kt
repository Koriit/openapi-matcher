package korrit.kotlin.openapi.model

import korrit.kotlin.openapi.YAML_INDENT

/**
 * Representation of [Response Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#responseObject).
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
            appendln()
            appendln("description: >-")
            append(description.prependIndent(YAML_INDENT))
            headers?.let {
                appendln()
                append("headers:")
                val out = StringBuilder()
                for (header in headers) {
                    out.apply {
                        appendln()
                        append(header.toString())
                    }
                }
                append(out.toString().prependIndent(YAML_INDENT))
            }
            content?.let {
                appendln()
                append("content:")
                for (media in content) {
                    appendln()
                    append(media.toString().prependIndent(YAML_INDENT))
                }
            }
        }

        return "\"$code\":" + spec.toString().prependIndent(YAML_INDENT)
    }
}
