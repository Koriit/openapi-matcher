package korrit.kotlin.openapi

const val OPENAPI_INDENT = "  "

private fun StringBuilder.newLine() = appendln()

//
// Limited default values serve the purpose of checking if everything is implemented by compiler
//

class OpenApi(
        val version: String = "3.0.2",
        val components: Components?,
        val paths: List<Path>
) {

    override fun toString(): String {
        return StringBuilder().apply {
            appendln("openapi: $version")
            appendln("paths:")
            paths.forEach {
                newLine()
                appendln(it.toString().prependIndent(OPENAPI_INDENT))
            }
            components?.let { append(components.toString()) }
        }.toString()
    }
}

class Components(
        val schemas: Map<String, Schema>?,
        val headers: Map<String, Header>?
) {

    override fun toString(): String {
        return "components:" + StringBuilder().apply {
            schemas?.let {
                newLine()
                append("schemas:")
                val out = StringBuilder()
                schemas.forEach { (name, schema) ->
                    out.apply {
                        newLine()
                        appendln("$name:")
                        append(schema.toString().prependIndent(OPENAPI_INDENT))
                    }
                }
                append(out.toString().prependIndent(OPENAPI_INDENT))
            }

            headers?.let {
                newLine()
                append("headers:")
                val out = StringBuilder()
                headers.forEach { (_, header) ->
                    out.apply {
                        newLine()
                        append(header.toString())
                    }
                }
                append(out.toString().prependIndent(OPENAPI_INDENT))
            }
        }.toString().prependIndent(OPENAPI_INDENT)
    }
}

class Path(
        val path: String,
        val operations: MutableList<Operation> = mutableListOf()
) {

    override fun toString(): String {
        return StringBuilder().apply {
            append("$path:")
            operations.forEach {
                newLine()
                append(it.toString().prependIndent(OPENAPI_INDENT))
            }
        }.toString()
    }
}

class Operation(
        val method: String,
        val responses: List<Response>,
        val requestBody: RequestBody?,
        val parameters: List<Parameter>?,
        val deprecated: Boolean
) {

    override fun toString(): String {
        return "$method:" + StringBuilder().apply {
            if (deprecated) {
                newLine()
                append("deprecated: true")
            }
            parameters?.let {
                newLine()
                append("parameters:")
                parameters.forEach {
                    newLine()
                    append("$OPENAPI_INDENT- ")
                    append(it.toString()
                            .prependIndent(OPENAPI_INDENT)
                            .prependIndent("  ")
                            .substring(OPENAPI_INDENT.length + 2))
                }
            }
            requestBody?.let {
                newLine()
                appendln("requestBody:")
                append(requestBody.toString().prependIndent(OPENAPI_INDENT))
            }
            newLine()
            append("responses:")
            responses.forEach {
                newLine()
                append(it.toString().prependIndent(OPENAPI_INDENT))
            }
        }.toString().prependIndent(OPENAPI_INDENT)
    }
}

class Parameter(
        val name: String,
        val `in`: String,
        val required: Boolean,
        val deprecated: Boolean,
        val description: String?,
        val schema: Schema
) {

    override fun toString(): String {
        return StringBuilder().apply {
            appendln("name: $name")
            append("in: $`in`")
            if (required) {
                newLine()
                append("required: true")
            }
            if (deprecated) {
                newLine()
                append("deprecated: true")
            }
            description?.let {
                newLine()
                append("description: $it")
            }
            newLine()
            appendln("schema:")
            append(schema.toString().prependIndent(OPENAPI_INDENT))
        }.toString()
    }
}

class Header(
        val name: String,
        val required: Boolean,
        val deprecated: Boolean,
        val schema: Schema
) {

    override fun toString(): String {
        return "$name:" + StringBuilder().apply {
            if (required) {
                newLine()
                append("required: true")
            }
            if (deprecated) {
                newLine()
                append("deprecated: true")
            }
            newLine()
            appendln("schema:")
            append(schema.toString().prependIndent(OPENAPI_INDENT))
        }.toString().prependIndent(OPENAPI_INDENT)
    }
}

class Response(
        val code: String,
        val content: List<MediaType>,
        val description: String,
        val headers: List<Header>?
) {

    override fun toString(): String {
        return "\"$code\":" + StringBuilder().apply {
            newLine()
            append("description: $description")
            headers?.let {
                newLine()
                append("headers:")
                val out = StringBuilder()
                headers.forEach {
                    out.apply {
                        newLine()
                        append(it.toString())
                    }
                }
                append(out.toString().prependIndent(OPENAPI_INDENT))
            }
            newLine()
            append("content:")
            content.forEach {
                newLine()
                append(it.toString().prependIndent(OPENAPI_INDENT))
            }
        }.toString().prependIndent(OPENAPI_INDENT)
    }
}

class MediaType(
        val contentType: String,
        val schema: Schema
) {

    override fun toString(): String {
        return "$contentType:" + StringBuilder().apply {
            newLine()
            appendln("schema:")
            append(schema.toString().prependIndent(OPENAPI_INDENT))
        }.toString().prependIndent(OPENAPI_INDENT)
    }
}

class Schema(
        val title: String?,
        val type: String,
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

    override fun toString(): String {
        return StringBuilder().apply {
            title?.let {
                appendln("title: $it")
            }
            append("type: $type")

            if (deprecated) {
                newLine()
                append("deprecated: true")
            }

            if (nullable) {
                newLine()
                append("nullable: true")
            }
            format?.let {
                newLine()
                append("format: $it")
            }
            default?.let {
                newLine()
                append("default: $it")
            }
            if (uniqueItems == true) {
                newLine()
                append("uniqueItems: true")
            }
            required?.let {
                newLine()
                append("required: $required")
            }
            properties?.let {
                newLine()
                append("properties:")
                it.forEach { prop ->
                    newLine()
                    append(prop.toString().prependIndent(OPENAPI_INDENT))
                }
            }
            additionalProperties?.let {
                newLine()
                appendln("additionalProperties:")
                append(it.toString().prependIndent(OPENAPI_INDENT))
            }
            items?.let {
                newLine()
                appendln("items:")
                append(it.toString().prependIndent(OPENAPI_INDENT))
            }
            enum?.let {
                newLine()
                append("enum: $it")
            }
        }.toString()
    }
}

class Property(
        val name: String,
        val schema: Schema
) {

    override fun toString(): String {
        return StringBuilder().apply {
            appendln("$name:")
            append(schema.toString().prependIndent(OPENAPI_INDENT))
        }.toString()
    }
}

class RequestBody(
        val content: List<MediaType>,
        val required: Boolean
) {

    override fun toString(): String {
        return StringBuilder().apply {
            if (required) {
                append("required: true")
                newLine()
            }
            append("content:")
            content.forEach {
                newLine()
                append(it.toString().prependIndent(OPENAPI_INDENT))
            }
        }.toString()
    }
}
