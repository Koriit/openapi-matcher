package com.korrit.kotlin.openapi.model

import com.korrit.kotlin.openapi.YAML_INDENT

/**
 * Each Media Type Object provides schema and examples for the media type identified by its key.
 *
 * Representation of [MediaType Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#mediaTypeObject).
 *
 * @property contentType Media type or media type range that is described by this object.
 * @property schema The schema defining the content of the request, response, or parameter.
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
    override fun toString(): String {
        val spec = StringBuilder().apply {
            schema?.let {
                val schemaSpec = schema.toString()
                if (schemaSpec.isNotBlank()) {
                    appendLine()
                    append("schema:")
                    append(schemaSpec.prependIndent(YAML_INDENT))
                }
            }
        }

        if (spec.isBlank()) {
            return "$contentType: {}"
        } else {
            return "$contentType:" + spec.toString().prependIndent(YAML_INDENT)
        }
    }
}
