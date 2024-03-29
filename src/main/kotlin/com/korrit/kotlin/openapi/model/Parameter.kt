package com.korrit.kotlin.openapi.model

import com.korrit.kotlin.openapi.YAML_INDENT

/**
 * Describes a single operation parameter.
 *
 * Representation of [Parameter Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#parameterObject).
 *
 * @property name The name of the parameter. Parameter names are case sensitive.
 * @property inside The location of the parameter. This is "in" field, renamed due to conflict with Kotlin keyword.
 * @property required Determines whether this parameter is mandatory.
 * @property deprecated Specifies that a parameter is deprecated and SHOULD be transitioned out of usage.
 * @property description A brief description of the parameter.
 * @property schema The schema defining the type used for the parameter.
 *
 * @WIP
 */
class Parameter(
    val name: String,
    val inside: String,
    val required: Boolean,
    val deprecated: Boolean,
    val description: String?,
    val schema: Schema
) {
    /**
     * Returns YAML representation.
     */
    override fun toString(): String = StringBuilder().apply {
        appendLine("name: $name")
        append("in: $inside")
        if (required) {
            appendLine()
            append("required: true")
        }
        if (deprecated) {
            appendLine()
            append("deprecated: true")
        }
        description?.let {
            appendLine()
            appendLine("description: >-")
            append(description.prependIndent(YAML_INDENT))
        }
        appendLine()
        append("schema:")
        append(schema.toString().prependIndent(YAML_INDENT))
    }.toString()
}
