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
    override fun toString(): String = "\"$code\":" + StringBuilder().apply {
        appendln()
        append("description: $description")
        headers?.let {
            appendln()
            append("headers:")
            val out = StringBuilder()
            headers.forEach {
                out.apply {
                    appendln()
                    append(it.toString())
                }
            }
            append(out.toString().prependIndent(YAML_INDENT))
        }
        content?.let {
            appendln()
            append("content:")
            content.forEach {
                appendln()
                append(it.toString().prependIndent(YAML_INDENT))
            }
        }
    }.toString().prependIndent(YAML_INDENT)
}
