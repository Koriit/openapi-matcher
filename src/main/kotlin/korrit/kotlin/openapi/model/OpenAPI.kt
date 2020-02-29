package korrit.kotlin.openapi.model

import korrit.kotlin.openapi.YAML_INDENT

/**
 * Representation of [OpenAPI Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#openapi-object).
 *
 * @WIP
 */
class OpenAPI(
    val version: String = "3.0.2",
    val components: Components?,
    val paths: List<Path>
) {
    /**
     * Returns YAML representation.
     */
    override fun toString(): String = StringBuilder().apply {
        appendln("openapi: $version")
        append("paths:")
        for(path in paths) {
            appendln()
            appendln(path.toString().prependIndent(YAML_INDENT))
        }
        components?.let {
            append(components.toString())
        }
    }.toString()
}
