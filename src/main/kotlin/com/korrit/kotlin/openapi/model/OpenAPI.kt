package com.korrit.kotlin.openapi.model

import com.korrit.kotlin.openapi.YAML_INDENT

/**
 * This is the root document object of the OpenAPI document.
 *
 * Representation of [OpenAPI Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#openapi-object).
 *
 * @property version Semantic version number of the OpenAPI Specification version that the OpenAPI document uses.
 * @property components An element to hold various schemas for the specification.
 * @property paths The available paths and operations for the API.
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
        appendLine("openapi: $version")
        append("paths:")
        for (path in paths) {
            appendLine()
            appendLine(path.toString().prependIndent(YAML_INDENT))
        }
        components?.let {
            append(components.toString())
        }
    }.toString()
}
