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

    override fun toString(): String = "$method:" + StringBuilder().apply {
        if (deprecated) {
            appendln()
            append("deprecated: true")
        }
        parameters?.let {
            appendln()
            append("parameters:")
            parameters.forEach {
                appendln()
                append("$YAML_INDENT- ")
                append(
                    it.toString()
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
        responses.forEach {
            appendln()
            append(it.toString().prependIndent(YAML_INDENT))
        }
    }.toString().prependIndent(YAML_INDENT)
}
