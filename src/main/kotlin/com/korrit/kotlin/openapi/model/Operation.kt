package com.korrit.kotlin.openapi.model

import com.korrit.kotlin.openapi.YAML_INDENT

/**
 * Describes a single API operation on a path.
 *
 * Representation of [Operation Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#operationObject).
 *
 * @property method HTTP method.
 * @property responses The list of possible responses as they are returned from executing this operation.
 * @property requestBody The request body applicable for this operation.
 * @property parameters A list of parameters that are applicable for this operation.
 * @property deprecated Declares this operation to be deprecated.
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
                appendLine()
                append("deprecated: true")
            }
            parameters?.let {
                appendLine()
                append("parameters:")
                for (parameter in parameters) {
                    appendLine()
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
                appendLine()
                appendLine("requestBody:")
                append(requestBody.toString().prependIndent(YAML_INDENT))
            }
            appendLine()
            append("responses:")
            for (response in responses) {
                appendLine()
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
