package korrit.kotlin.openapi.model

import korrit.kotlin.openapi.YAML_INDENT

/**
 * Representation of [Path Item Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#pathItemObject).
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
        for(operation in operations) {
            appendln()
            append(operation.toString().prependIndent(YAML_INDENT))
        }
    }.toString()
}
