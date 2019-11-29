package korrit.kotlin.openapi.model

import korrit.kotlin.openapi.YAML_INDENT

/**
 * Representation of [Schema Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject).
 *
 * @WIP
 */
class Schema(
    val title: String?,
    val type: String?,
    val deprecated: Boolean,
    val nullable: Boolean,
    val format: String?,
    val default: Any?,
    val uniqueItems: Boolean?,
    val required: List<String>?,
    val properties: List<Property>?,
    val additionalProperties: Schema?,
    val items: Schema?,
    val enum: List<String>?
) {

    override fun toString(): String = StringBuilder().apply {
        title?.let {
            append("title: $it")
        }
        type?.let {
            appendln()
            append("type: $type")
        }
        if (deprecated) {
            appendln()
            append("deprecated: true")
        }

        if (nullable) {
            appendln()
            append("nullable: true")
        }
        format?.let {
            appendln()
            append("format: $it")
        }
        default?.let {
            appendln()
            append("default: $it")
        }
        if (uniqueItems == true) {
            appendln()
            append("uniqueItems: true")
        }
        required?.let {
            appendln()
            append("required: $required")
        }
        properties?.let {
            appendln()
            append("properties:")
            it.forEach { prop ->
                appendln()
                append(prop.toString().prependIndent(YAML_INDENT))
            }
        }
        additionalProperties?.let {
            appendln()
            appendln("additionalProperties:")
            append(it.toString().prependIndent(YAML_INDENT))
        }
        items?.let {
            appendln()
            appendln("items:")
            append(it.toString().prependIndent(YAML_INDENT))
        }
        enum?.let {
            appendln()
            append("enum: $it")
        }
    }.toString()
}
