package korrit.kotlin.openapi.model

import korrit.kotlin.openapi.YAML_INDENT

/**
 * Representation of [Path Item Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#pathItemObject).
 *
 * @WIP
 */
class Path(
    val path: String,
    val operations: MutableList<Operation> = mutableListOf()
) {

    override fun toString(): String = StringBuilder().apply {
        append("$path:")
        operations.forEach {
            appendln()
            append(it.toString().prependIndent(YAML_INDENT))
        }
    }.toString()
}
