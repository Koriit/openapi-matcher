package korrit.kotlin.openapi.model

import korrit.kotlin.openapi.YAML_INDENT

/**
 * Representation of [Operation Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#operationObject).
 *
 * @WIP
 */
class Operation(
    val method: String,
    val responses: List<Response>,
    val requestBody: RequestBody?,
    val parameters: List<Parameter>?,
    val deprecated: Boolean
) {
    /**
     * Returns YAML representation.
     */
    override fun toString(): String {
        val spec = StringBuilder().apply {
            if (deprecated) {
                appendln()
                append("deprecated: true")
            }
            parameters?.let {
                appendln()
                append("parameters:")
                for (parameter in parameters) {
                    appendln()
                    append("$YAML_INDENT- ")
                    append(
                        parameter.toString()
                            .prependIndent(YAML_INDENT)
                            .prependIndent("  ")
                            .substring(YAML_INDENT.length + 2)
                    )
                }
            }
            requestBody?.let {
                appendln()
                appendln("requestBody:")
                append(requestBody.toString().prependIndent(YAML_INDENT))
            }
            appendln()
            append("responses:")
            for (response in responses) {
                appendln()
                append(response.toString().prependIndent(YAML_INDENT))
            }
        }

        if (spec.isBlank()) {
            return "$method: {}"
        } else {
            return "$method:" + spec.toString().prependIndent(YAML_INDENT)
        }
    }
}
