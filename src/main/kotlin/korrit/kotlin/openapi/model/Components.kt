package korrit.kotlin.openapi.model

import korrit.kotlin.openapi.YAML_INDENT

/**
 * Representation of [Components Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#componentsObject).
 *
 * @WIP
 */
class Components(
    val schemas: Map<String, Schema>?,
    val headers: Map<String, Header>?
) {
    /**
     * Returns YAML representation.
     */
    override fun toString(): String = "components:" + StringBuilder().apply {
        schemas?.let {
            appendln()
            append("schemas:")
            val out = StringBuilder()
            schemas.forEach { (name, schema) ->
                out.apply {
                    appendln()
                    append("$name:")
                    append(schema.toString().prependIndent(YAML_INDENT))
                }
            }
            append(out.toString().prependIndent(YAML_INDENT))
        }

        headers?.let {
            appendln()
            append("headers:")
            val out = StringBuilder()
            headers.forEach { (_, header) ->
                out.apply {
                    appendln()
                    append(header.toString())
                }
            }
            append(out.toString().prependIndent(YAML_INDENT))
        }
    }.toString().prependIndent(YAML_INDENT)
}
