package korrit.kotlin.openapi.model

import korrit.kotlin.openapi.YAML_INDENT

/**
 * Representation of [MediaType Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#mediaTypeObject).
 *
 * @WIP
 */
class MediaType(
    val contentType: String,
    val schema: Schema?
) {
    /**
     * Returns YAML representation.
     */
    override fun toString(): String = "$contentType:" + StringBuilder().apply {
        schema?.let {
            appendln()
            append("schema:")
            append(schema.toString().prependIndent(YAML_INDENT))
        }
    }.toString().prependIndent(YAML_INDENT)
}
