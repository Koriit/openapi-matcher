package com.korrit.kotlin.openapi.model

import com.korrit.kotlin.openapi.YAML_INDENT

/**
 * Describes the operations available on a single path.
 *
 * Representation of [Path Item Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#pathItemObject).
 *
 * @property path The HTTP path.
 * @property operations The available HTTP operations.
 *
 * @WIP
 */
class Path(
    val path: String,
    val operations: List<Operation>
) {
    /**
     * Returns YAML representation.
     */
    override fun toString(): String = StringBuilder().apply {
        append("$path:")
        for (operation in operations) {
            appendLine()
            append(operation.toString().prependIndent(YAML_INDENT))
        }
    }.toString()
}
