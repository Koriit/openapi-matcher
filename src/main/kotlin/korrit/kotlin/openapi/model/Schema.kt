package korrit.kotlin.openapi.model

import korrit.kotlin.openapi.YAML_INDENT

/**
 * The Schema Object allows the definition of input and output data types.
 * These types can be objects, but also primitives and arrays.
 *
 * Representation of [Schema Object](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#schemaObject).
 *
 * @property title Title of the schema.
 * @property type Primitive type of the schema.
 * @property deprecated Specifies that a schema is deprecated and SHOULD be transitioned out of usage.
 * @property nullable Allows sending a null value for the defined schema.
 * @property format Interoperable semantic validation for a fixed subset of values which are accurately described by given format.
 * @property default The default value represents what would be assumed if one is not provided.
 * @property uniqueItems Requires all of instance's elements to be unique
 * @property required An object instance is valid against this keyword if its property set contains all elements in this keyword's array value.
 * @property properties Defines object instance properties.
 * @property additionalProperties Schema of eventual additional properties.
 * @property items Schema of instance's elements.
 * @property enum If present, instance value must be one of elements of this list.
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
    /**
     * Returns YAML representation.
     *
     * Starts with new line in the beginning.
     */
    @Suppress("LongMethod", "ComplexMethod") // There is lots of elements, so naturally this happens
    override fun toString(): String {
        val spec = StringBuilder().apply {
            title?.let {
                appendLine()
                append("title: $it")
            }
            type?.let {
                appendLine()
                append("type: $type")
            }
            if (deprecated) {
                appendLine()
                append("deprecated: true")
            }

            if (nullable) {
                appendLine()
                append("nullable: true")
            }
            format?.let {
                appendLine()
                append("format: $it")
            }
            default?.let {
                appendLine()
                val value = when (default) {
                    is String -> default.replace("\"", "\\\"")
                    else -> default.toString()
                }
                append("default: \"$value\"")
            }
            if (uniqueItems == true) {
                appendLine()
                append("uniqueItems: true")
            }
            required?.let {
                appendLine()
                append("required: $required")
            }
            properties?.let {
                appendLine()
                append("properties:")
                for (property in properties) {
                    appendLine()
                    append(property.toString().prependIndent(YAML_INDENT))
                }
            }
            additionalProperties?.let {
                appendLine()
                append("additionalProperties:")
                append(it.toString().prependIndent(YAML_INDENT))
            }
            items?.let {
                appendLine()
                append("items:")
                append(it.toString().prependIndent(YAML_INDENT))
            }
            enum?.let {
                appendLine()
                append("enum: $it")
            }
        }

        return if (spec.isBlank()) "{}" else spec.toString()
    }
}
