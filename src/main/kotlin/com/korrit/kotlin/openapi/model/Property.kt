package com.korrit.kotlin.openapi.model

import com.korrit.kotlin.openapi.YAML_INDENT

/**
 * Pair of key and [Schema Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject).
 *
 * @property name Property name.
 * @property schema Property schema.
 */
class Property(
    val name: String,
    val schema: Schema
) {
    /**
     * Returns YAML representation.
     */
    override fun toString(): String = StringBuilder().apply {
        append("$name:")
        append(schema.toString().prependIndent(YAML_INDENT))
    }.toString()
}
