@file:Suppress("UNCHECKED_CAST")

package korrit.kotlin.openapi

import java.io.InputStream
import korrit.kotlin.openapi.exceptions.ReaderException
import korrit.kotlin.openapi.exceptions.UnknownRef
import korrit.kotlin.openapi.model.Components
import korrit.kotlin.openapi.model.Header
import korrit.kotlin.openapi.model.MediaType
import korrit.kotlin.openapi.model.OpenAPI
import korrit.kotlin.openapi.model.Operation
import korrit.kotlin.openapi.model.Parameter
import korrit.kotlin.openapi.model.Path
import korrit.kotlin.openapi.model.Property
import korrit.kotlin.openapi.model.RequestBody
import korrit.kotlin.openapi.model.Response
import korrit.kotlin.openapi.model.Schema
import org.yaml.snakeyaml.Yaml

private typealias YAML = Map<String, Any>

private fun <T> YAML.getAs(elem: String): T = get(elem) as T
private fun <T : Any> YAML.getAs(elem: String, default: T): T = get(elem) as T? ?: default

/**
 * OpenAPI reader. Reads your OpenAPI specification in YAML format and returns in-memory object representation of it.
 *
 * This reader holds internal state for resolution of references and thus it is not thread safe(NTS).
 */
open class OpenAPIReader {

    protected open var schemas: Map<String, Schema> = emptyMap()
    protected open var headers: Map<String, Header> = emptyMap()

    /**
     * Accepts YAML file and returns OpenAPI object.
     *
     * @throws ReaderException in case of any failure
     */
    @Suppress("TooGenericExceptionCaught") // Intended
    open fun load(file: InputStream): OpenAPI {
        try {
            val openapi: YAML = Yaml().load(file)
            schemas = emptyMap()
            headers = emptyMap()

            return readOpenAPI(openapi)
        } catch (e: ReaderException) {
            throw e
        } catch (e: Exception) {
            throw ReaderException(e.message ?: "", e)
        }
    }

    /**
     * Accepts YAML string and returns OpenAPI object.
     *
     * @throws ReaderException in case of any failure
     */
    open fun load(yaml: String): OpenAPI {
        return load(yaml.byteInputStream())
    }

    protected open fun readOpenAPI(openapi: YAML): OpenAPI {
        val components = openapi.getAs<YAML?>("components")?.let { readComponents(it) }
        val paths = readPaths(openapi)

        return OpenAPI(version = openapi.getAs("openapi"), components = components, paths = paths)
    }

    protected open fun readComponents(components: YAML): Components {
        val schemas: Map<String, Schema>? = components.getAs<YAML?>("schemas")?.let {
            readComponentSchemas(it)
        }

        val headers: Map<String, Header>? = components.getAs<YAML?>("headers")?.let {
            readComponentHeaders(it)
        }

        return Components(schemas, headers)
    }

    private fun readComponentHeaders(headers: YAML): MutableMap<String, Header> {
        val resolved: MutableMap<String, Header> = mutableMapOf()
        this.headers = resolved
        val unresolved = headers.toMutableMap()

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
        return resolved
    }

    private fun readComponentSchemas(schemas: YAML): MutableMap<String, Schema> {
        val resolved: MutableMap<String, Schema> = mutableMapOf()
        this.schemas = resolved
        val unresolved = schemas.toMutableMap()

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
        return resolved
    }

    protected open fun readPaths(openapi: YAML): List<Path> {
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

    protected open fun readOperation(method: String, operation: YAML): Operation {
        val responses = readResponses(operation)
        val requestBody = readRequestBody(operation)
        val deprecated = operation.getAs("deprecated", false)
        val parameters = readParameters(operation)

        return Operation(method, responses, requestBody, parameters, deprecated)
    }

    protected open fun readParameters(operation: YAML): List<Parameter>? {
        return operation.getAs<List<YAML>?>("parameters")?.map {
            val name = it.getAs<String>("name")
            val inside = it.getAs<String>("in")
            val required = it.getAs("required", false)
            val deprecated = it.getAs("deprecated", false)
            val description = it.getAs<String?>("description")
            val schema = readSchema(it.getAs("schema"))

            Parameter(name, inside, required, deprecated, description, schema)
        }
    }

    protected open fun readResponses(operation: YAML): List<Response> {
        return operation.getAs<YAML>("responses").map { (code, response) ->
            response as YAML
            val headers = response.getAs<YAML?>("headers")?.map { (name, header) ->
                readHeader(name, header as YAML)
            }

            Response(code, readContent(response), response.getAs("description"), headers)
        }
    }

    protected open fun readHeader(name: String, header: YAML): Header {
        val ref = header.getAs<String?>("\$ref")
        if (ref != null) {
            val headerName = """#/components/headers/(.*)"""
                    .toRegex()
                    .find(ref)
                    ?.groupValues?.get(1)
                    ?: throw ReaderException("Cannot parse headers reference: $ref")

            return headers[headerName] ?: throw UnknownRef(ref)
        }

        val required = header.getAs("required", false)
        val deprecated = header.getAs("deprecated", false)
        val schema = readSchema(header.getAs("schema"))

        return Header(name, required, deprecated, schema)
    }

    protected open fun readRequestBody(operation: YAML): RequestBody? {
        return operation.getAs<YAML?>("requestBody")?.let {
            val content = readContent(it)
            RequestBody(content, it.getAs("required", false))
        }
    }

    protected open fun readContent(obj: YAML): List<MediaType> {
        return obj.getAs<YAML?>("content")?.map { (contentType, media) ->
            readMediaType(contentType, media as YAML)
        } ?: emptyList()
    }

    protected open fun readMediaType(contentType: String, mediaType: YAML): MediaType {
        val schema = mediaType.getAs<YAML?>("schema")

        return MediaType(contentType, schema?.let { readSchema(schema) })
    }

    protected open fun readSchema(schema: YAML): Schema {
        val ref = schema.getAs<String?>("\$ref")
        if (ref != null) {
            val schemaName = """#/components/schemas/(.*)"""
                    .toRegex()
                    .find(ref)
                    ?.groupValues?.get(1)
                    ?: throw ReaderException("Cannot parse schema reference: $ref")

            return schemas[schemaName] ?: throw UnknownRef(ref)
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

    protected open fun readProperties(properties: YAML): List<Property> {
        return properties.map { (name, schema) ->
            Property(name, readSchema(schema as YAML))
        }
    }
}
