package korrit.kotlin.openapi

import java.io.InputStream
import org.yaml.snakeyaml.Yaml

private typealias YAML = Map<String, Any>

private fun <T> YAML.getAs(elem: String): T = get(elem) as T
private fun <T : Any> YAML.getAs(elem: String, default: T): T = get(elem) as T? ?: default

val OPENAPI_OPERATIONS = listOf("get", "post", "put", "patch", "delete", "head", "options", "trace")

class UnknownRef(ref: String) : RuntimeException("Cannot resolve reference: $ref")
class ReaderException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

open class OpenApiReader(private val file: InputStream) {

    private var _schemas: Map<String, Schema> = emptyMap()
    private var _headers: Map<String, Header> = emptyMap()

    fun load(): OpenApi {
        try {
            val openapi: YAML = Yaml().load(file)

            return readOpenApi(openapi)
        } catch (e: ReaderException) {
            throw e
        } catch (e: Exception) {
            throw ReaderException(e.message ?: "", e)
        }
    }

    protected fun readOpenApi(openapi: YAML): OpenApi {
        val components = openapi.getAs<YAML?>("components")?.let { readComponents(it) }
        val paths = readPaths(openapi)

        return OpenApi(version = openapi.getAs("openapi"), components = components, paths = paths)
    }

    protected fun readComponents(components: YAML): Components {
        val schemas: Map<String, Schema>? = components.getAs<YAML?>("schemas")?.let {
            val resolved: MutableMap<String, Schema> = mutableMapOf()
            _schemas = resolved
            val unresolved = it.toMutableMap()

            var remaining = unresolved.size
            while (unresolved.isNotEmpty()) {
                var lastErr: Exception? = null
                unresolved.toMap().forEach { (name, schema) ->
                    try {
                        resolved[name] = readSchema(schema as YAML)
                        unresolved.remove(name)
                    } catch (e: UnknownRef) {
                        lastErr = e
                    }
                }
                if (remaining <= unresolved.size) {
                    throw ReaderException("Cannot resolve some components schemas(probably cyclic references): " + unresolved.keys, lastErr)
                } else {
                    remaining = unresolved.size
                }
            }
            resolved
        }

        val headers: Map<String, Header>? = components.getAs<YAML?>("headers")?.let {
            val resolved: MutableMap<String, Header> = mutableMapOf()
            _headers = resolved
            val unresolved = it.toMutableMap()

            var remaining = unresolved.size
            while (unresolved.isNotEmpty()) {
                var lastErr: Exception? = null
                unresolved.toMap().forEach { (name, schema) ->
                    try {
                        resolved[name] = readHeader(name, schema as YAML)
                        unresolved.remove(name)
                    } catch (e: UnknownRef) {
                        lastErr = e
                    }
                }
                if (remaining <= unresolved.size) {
                    throw ReaderException("Cannot resolve some components headers(probably cyclic references): " + unresolved.keys, lastErr)
                } else {
                    remaining = unresolved.size
                }
            }
            resolved
        }

        return Components(schemas, headers)
    }

    protected fun readPaths(openapi: YAML): List<Path> {
        return openapi.getAs<YAML>("paths").map { (path, operations) ->
            Path(path).apply {
                (operations as YAML).forEach { (method, operation) ->
                    if (method !in OPENAPI_OPERATIONS) {
                        return@forEach
                    }

                    this.operations.add(readOperation(method, operation as YAML))
                }
            }
        }
    }

    protected fun readOperation(method: String, operation: YAML): Operation {
        val responses = readResponses(operation)
        val requestBody = readRequestBody(operation)
        val deprecated = operation.getAs("deprecated", false)
        val parameters = readParameters(operation)

        return Operation(method, responses, requestBody, parameters, deprecated)
    }

    protected fun readParameters(operation: YAML): List<Parameter>? {
        return operation.getAs<List<YAML>?>("parameters")?.map {
            val name = it.getAs<String>("name")
            val `in` = it.getAs<String>("in")
            val required = it.getAs("required", false)
            val deprecated = it.getAs("deprecated", false)
            val description = it.getAs<String?>("description")
            val schema = readSchema(it.getAs("schema"))

            Parameter(name, `in`, required, deprecated, description, schema)
        }
    }

    protected fun readResponses(operation: YAML): List<Response> {
        return operation.getAs<YAML>("responses").map { (code, response) ->
            response as YAML
            val headers = response.getAs<YAML?>("headers")?.map { (name, header) ->
                readHeader(name, header as YAML)
            }

            Response(code, readContent(response), response.getAs("description"), headers)
        }
    }

    protected fun readHeader(name: String, header: YAML): Header {
        val ref = header.getAs<String?>("\$ref")
        if (ref != null) {
            val headerName = """#/components/headers/(.*)"""
                    .toRegex()
                    .find(ref)
                    ?.groupValues?.get(1)
                    ?: throw ReaderException("Cannot parse headers reference: $ref")

            return _headers[headerName] ?: throw UnknownRef(ref)
        }

        val required = header.getAs("required", false)
        val deprecated = header.getAs("deprecated", false)
        val schema = readSchema(header.getAs("schema"))

        return Header(name, required, deprecated, schema)
    }

    protected fun readRequestBody(operation: YAML): RequestBody? {
        return operation.getAs<YAML?>("requestBody")?.let {
            val content = readContent(it)
            RequestBody(content, it.getAs("required", false))
        }
    }

    protected fun readContent(obj: YAML): List<MediaType> {
        return obj.getAs<YAML?>("content")?.map { (contentType, media) ->
            readMediaType(contentType, media as YAML)
        } ?: emptyList()
    }

    protected fun readMediaType(contentType: String, mediaType: YAML): MediaType {
        return MediaType(contentType, readSchema(mediaType.getAs("schema")))
    }

    protected fun readSchema(schema: YAML): Schema {
        val ref = schema.getAs<String?>("\$ref")
        if (ref != null) {
            val schemaName = """#/components/schemas/(.*)"""
                    .toRegex()
                    .find(ref)
                    ?.groupValues?.get(1)
                    ?: throw ReaderException("Cannot parse schema reference: $ref")

            return _schemas[schemaName] ?: throw UnknownRef(ref)
        }

        val title = schema.getAs<String?>("title")
        val type = schema.getAs<String>("type")
        val deprecated = schema.getAs("deprecated", false)
        val nullable = schema.getAs("nullable", false)
        val format = schema.getAs<String?>("format")
        val default = schema.getAs<Any?>("default")
        val uniqueItems = schema.getAs<Boolean?>("uniqueItems")
        val required = schema.getAs<List<String>?>("required")
        val properties: List<Property>? = schema.getAs<YAML?>("properties")?.let { readProperties(it) }
        val additionalProperties: Schema? = schema.getAs<YAML?>("additionalProperties")?.let { readSchema(it) }
        val items: Schema? = schema.getAs<YAML?>("items")?.let { readSchema(it) }
        val enum = schema.getAs<List<String>?>("enum")

        return Schema(title, type, deprecated, nullable, format, default, uniqueItems, required, properties, additionalProperties, items, enum)
    }

    protected fun readProperties(properties: YAML): List<Property> {
        return properties.map { (name, schema) ->
            Property(name, readSchema(schema as YAML))
        }
    }
}
