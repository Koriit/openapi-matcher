package korrit.kotlin.openapi.model

import korrit.kotlin.openapi.YAML_INDENT

/**
 * Holds a set of reusable objects for different aspects of the OAS.
 *
 * Representation of [Components Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#componentsObject).
 *
 * @property schemas Reusable Schema Objects, mapped by schema name.
 * @property headers Reusable Header Objects, mapped by header name.
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
    override fun toString(): String {
        val spec = StringBuilder().apply {
            schemas?.let {
                appendLine()
                append("schemas:")
                val out = StringBuilder()
                for ((name, schema) in schemas) {
                    out.apply {
                        appendLine()
                        append("$name:")
                        append(schema.toString().prependIndent(YAML_INDENT))
                    }
                }
                append(out.toString().prependIndent(YAML_INDENT))
            }

            headers?.let {
                appendLine()
                append("headers:")
                val out = StringBuilder()
                for ((_, header) in headers) {
                    out.apply {
                        appendLine()
                        append(header.toString())
                    }
                }
                append(out.toString().prependIndent(YAML_INDENT))
            }
        }

        if (spec.isBlank()) {
            return "components: {}"
        } else {
            return "components:" + spec.toString().prependIndent(YAML_INDENT)
        }
    }
}
