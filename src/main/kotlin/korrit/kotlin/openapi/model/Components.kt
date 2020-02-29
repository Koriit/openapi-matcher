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
    override fun toString(): String {
        val spec = StringBuilder().apply {
            schemas?.let {
                appendln()
                append("schemas:")
                val out = StringBuilder()
                for ((name, schema) in schemas) {
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
                for ((_, header) in headers) {
                    out.apply {
                        appendln()
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
