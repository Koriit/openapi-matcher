package com.korrit.kotlin.openapi

import com.korrit.kotlin.openapi.model.Header
import com.korrit.kotlin.openapi.model.MediaType
import com.korrit.kotlin.openapi.model.OpenAPI
import com.korrit.kotlin.openapi.model.Operation
import com.korrit.kotlin.openapi.model.Parameter
import com.korrit.kotlin.openapi.model.Path
import com.korrit.kotlin.openapi.model.Property
import com.korrit.kotlin.openapi.model.RequestBody
import com.korrit.kotlin.openapi.model.Response
import com.korrit.kotlin.openapi.model.Schema

private typealias Errors = MutableList<String>

/**
 *  OpenAPI Matcher to check differences between specifications.
 */
open class OpenAPIMatcher {

    /**
     * Matches 2 OpenAPI objects and returns the list of significant differences as errors.
     *
     * Significant difference is understood as divergence that leads or may lead to compatibility break.
     */
    open fun match(doc: OpenAPI, source: OpenAPI): List<String> {
        val errors: Errors = mutableListOf()

        if (doc.version != source.version) {
            errors.add("Doc's OpenAPI version doesn't match source's: ${doc.version} != ${source.version}")
        }

        val unmatchedPaths = doc.paths.map { it.path }.toMutableSet()
        source.paths.forEach { path ->
            val pathDoc = doc.paths.find { it.path == path.path }
            if (pathDoc == null) {
                errors.add("Cannot find doc for path: ${path.path}")
                return@forEach
            }
            unmatchedPaths.remove(pathDoc.path)

            errors.addAll(validatePath(pathDoc, path))
        }

        if (unmatchedPaths.isNotEmpty()) {
            errors.add("There are unknown paths in the doc: $unmatchedPaths")
        }

        return errors
    }

    protected open fun validatePath(doc: Path, source: Path): List<String> {
        val errors: Errors = mutableListOf()
        fun addError(error: String) = errors.add("In path ${source.path}: $error")

        val unmatchedOperations = doc.operations.map { it.method }.toMutableSet()
        source.operations.forEach { operation ->
            val operationDoc = doc.operations.find { it.method == operation.method }
            if (operationDoc == null) {
                addError("Cannot find doc for operation: ${operation.method}")
                return@forEach
            }
            unmatchedOperations.remove(operationDoc.method)

            validateOperation(operationDoc, operation).forEach {
                addError(it)
            }
        }

        if (unmatchedOperations.isNotEmpty()) {
            addError("There are unknown operations in the doc: $unmatchedOperations")
        }

        return errors
    }

    @Suppress("ComplexMethod")
    protected open fun validateOperation(doc: Operation, source: Operation): List<String> {
        val errors: Errors = mutableListOf()
        fun addError(error: String) = errors.add("In operation ${source.method}: $error")

        // deprecated
        if (doc.deprecated != source.deprecated) {
            addError("Doc's deprecation doesn't match source's: ${doc.deprecated} != ${source.deprecated}")
        }

        // requestBody
        if (doc.requestBody == null && source.requestBody != null) {
            addError("Doc is missing requestBody definition")
        } else if (doc.requestBody != null && source.requestBody == null) {
            addError("There is unknown requestBody in the doc")
        } else if (doc.requestBody != null && source.requestBody != null) {
            validateRequestBody(doc.requestBody, source.requestBody).forEach {
                addError(it)
            }
        }

        // parameters
        if (doc.parameters == null && source.parameters != null) {
            addError("Doc is entirely missing parameters definition")
        } else if (doc.parameters != null && source.parameters == null) {
            addError("Doc has unknown parameters definition")
        } else if (doc.parameters != null && source.parameters != null) {
            val unmatchedParameters = doc.parameters.associateBy { it.name }.toMutableMap()
            source.parameters.forEach { param ->
                val paramDoc = unmatchedParameters.remove(param.name)
                if (paramDoc == null) {
                    addError("Cannot find doc for parameter: ${param.name}")
                    return@forEach
                }

                validateParameter(paramDoc, param).forEach {
                    addError("In parameters: $it")
                }
            }

            if (unmatchedParameters.isNotEmpty()) {
                addError("There are unknown parameters in the doc: ${unmatchedParameters.keys}")
            }
        }

        // responses
        val unmatchedResponses = doc.responses.associateBy { it.code }.toMutableMap()
        source.responses.forEach { response ->
            val responseDoc = unmatchedResponses.remove(response.code)
            if (responseDoc == null) {
                addError("Cannot find doc for response: ${response.code}")
                return@forEach
            }

            validateResponse(responseDoc, response).forEach {
                addError("In responses: $it")
            }
        }

        if (unmatchedResponses.isNotEmpty()) {
            addError("There are unknown responses in the doc: ${unmatchedResponses.keys}")
        }

        return errors
    }

    protected open fun validateParameter(doc: Parameter, source: Parameter): List<String> {
        val errors: Errors = mutableListOf()
        fun addError(error: String) = errors.add("In parameter ${source.name}: $error")

        if (doc.name != source.name) {
            addError("Doc's name doesn't match source's: ${doc.name} != ${source.name}")
        }

        if (doc.inside != source.inside) {
            addError("Doc's 'in' doesn't match source's: ${doc.inside} != ${source.inside}")
        }

        if (doc.required != source.required) {
            addError("Doc's required doesn't match source's: ${doc.required} != ${source.required}")
        }

        if (doc.deprecated != source.deprecated) {
            addError("Doc's deprecated doesn't match source's: ${doc.deprecated} != ${source.deprecated}")
        }

        validateSchema(doc.schema, source.schema).forEach {
            addError("In schema: $it")
        }

        return errors
    }

    @Suppress("ComplexMethod")
    protected open fun validateResponse(doc: Response, source: Response): List<String> {
        val errors: Errors = mutableListOf()
        fun addError(error: String) = errors.add("In response ${source.code}: $error")

        // description
        if (doc.description.isEmpty()) {
            addError("Doc's description is empty")
        }

        // content
        val unmatchedContents = doc.content?.map { it.contentType }?.toMutableSet() ?: mutableSetOf()
        source.content?.forEach { content ->
            val contentDoc = doc.content?.find { it.contentType == content.contentType }
            if (contentDoc == null) {
                addError("Cannot find doc for content: ${content.contentType}")
                return@forEach
            }
            unmatchedContents.remove(contentDoc.contentType)

            validateMediaType(contentDoc, content).forEach {
                addError(it)
            }
        }

        if (unmatchedContents.isNotEmpty()) {
            addError("There are unknown contents in the doc: $unmatchedContents")
        }

        // headers
        if (doc.headers == null && source.headers != null) {
            addError("Doc is entirely missing headers definition")
        } else if (doc.headers != null && source.headers == null) {
            addError("Doc has unknown headers definition")
        } else if (doc.headers != null && source.headers != null) {
            val unmatchedHeaders = doc.headers.associateBy { it.name }.toMutableMap()
            source.headers.forEach { header ->
                val headerDoc = unmatchedHeaders.remove(header.name)
                if (headerDoc == null) {
                    addError("Cannot find doc for header: ${header.name}")
                    return@forEach
                }

                validateHeader(headerDoc, header).forEach {
                    addError("In headers: $it")
                }
            }

            if (unmatchedHeaders.isNotEmpty()) {
                addError("There are unknown headers in the doc: ${unmatchedHeaders.keys}")
            }
        }

        return errors
    }

    protected open fun validateHeader(doc: Header, source: Header): List<String> {
        val errors: Errors = mutableListOf()
        fun addError(error: String) = errors.add("In header ${source.name}: $error")

        if (doc.name != source.name) {
            addError("Doc's name doesn't match source's: ${doc.name} != ${source.name}")
        }

        if (doc.required != source.required) {
            addError("Doc's required doesn't match source's: ${doc.required} != ${source.required}")
        }

        if (doc.deprecated != source.deprecated) {
            addError("Doc's deprecated doesn't match source's: ${doc.deprecated} != ${source.deprecated}")
        }

        validateSchema(doc.schema, source.schema).forEach {
            addError("In schema: $it")
        }

        return errors
    }

    protected open fun validateMediaType(doc: MediaType, source: MediaType): List<String> {
        val errors: Errors = mutableListOf()
        fun addError(error: String) = errors.add("In content ${source.contentType}: $error")

        if (doc.schema == null || source.schema == null) {
            if (doc.schema != null && source.schema == null) {
                addError("Doc has unknown schema")
            } else if (doc.schema == null && source.schema != null) {
                addError("Doc is missing schema")
            }

            return errors
        }

        validateSchema(doc.schema, source.schema).forEach {
            addError("In schema: $it")
        }

        return errors
    }

    @Suppress("ComplexMethod", "LongMethod")
    protected open fun validateSchema(doc: Schema, source: Schema): List<String> {
        val errors: Errors = mutableListOf()

        fun addError(error: String) = errors.add(error)
        fun <T> List<T>?.toSetOrEmpty() = this?.toSet() ?: emptySet()

        if (doc.type != source.type) {
            addError("Doc's type doesn't match source's: ${doc.type} != ${source.type}")
        }

        if (doc.deprecated != source.deprecated) {
            addError("Doc's deprecated doesn't match source's: ${doc.deprecated} != ${source.deprecated}")
        }

        if (doc.nullable != source.nullable) {
            addError("Doc's nullable doesn't match source's: ${doc.nullable} != ${source.nullable}")
        }

        if (doc.format != source.format) {
            addError("Doc's format doesn't match source's: ${doc.format} != ${source.format}")
        }

        if (doc.default?.toString() != source.default?.toString()) {
            addError("Doc's default doesn't match source's: ${doc.default} != ${source.default}")
        }

        if (doc.uniqueItems != source.uniqueItems) {
            addError("Doc's uniqueItems doesn't match source's: ${doc.uniqueItems} != ${source.uniqueItems}")
        }

        if (doc.required != null || source.required != null) {
            val missing = source.required.toSetOrEmpty() - doc.required.toSetOrEmpty()
            if (missing.isNotEmpty()) {
                addError("Doc is missing required properties: $missing")
            }

            val unknown = doc.required.toSetOrEmpty() - source.required.toSetOrEmpty()
            if (unknown.isNotEmpty()) {
                addError("Doc has unknown required properties: $unknown")
            }
        }

        if (doc.properties == null && source.properties != null) {
            addError("Doc is entirely missing properties definition")
        } else if (doc.properties != null && source.properties == null) {
            addError("Doc has unknown properties definition")
        } else if (doc.properties != null && source.properties != null) {
            validateProperties(doc.properties, source.properties).forEach {
                addError("In properties: $it")
            }
        }

        if (doc.additionalProperties == null && source.additionalProperties != null) {
            addError("Doc is entirely missing additionalProperties definition")
        } else if (doc.additionalProperties != null && source.additionalProperties == null) {
            addError("Doc has unknown additionalProperties definition")
        } else if (doc.additionalProperties != null && source.additionalProperties != null) {
            validateSchema(doc.additionalProperties, source.additionalProperties).forEach {
                addError("In additionalProperties: $it")
            }
        }

        if (doc.items == null && source.items != null) {
            addError("Doc is entirely missing items definition")
        } else if (doc.items != null && source.items == null) {
            addError("Doc has unknown items definition")
        } else if (doc.items != null && source.items != null) {
            validateSchema(doc.items, source.items).forEach {
                addError("In items: $it")
            }
        }

        if (doc.enum != null || source.enum != null) {
            val missing = source.enum.toSetOrEmpty() - doc.enum.toSetOrEmpty()
            if (missing.isNotEmpty()) {
                addError("Doc is missing enum values: $missing")
            }

            val unknown = doc.enum.toSetOrEmpty() - source.enum.toSetOrEmpty()
            if (unknown.isNotEmpty()) {
                addError("Doc has unknown enum values: $unknown")
            }
        }

        return errors
    }

    protected open fun validateProperties(doc: List<Property>, source: List<Property>): List<String> {
        val errors: Errors = mutableListOf()
        fun addError(error: String) = errors.add(error)

        val unmatched = source.associateBy { it.name }.toMutableMap()
        doc.forEach { prop ->
            val src = unmatched.remove(prop.name)
            if (src == null) {
                addError("Unknown property: ${prop.name}")
            } else {
                validateProperty(prop, src).forEach {
                    addError(it)
                }
            }
        }

        if (unmatched.isNotEmpty()) {
            addError("Doc is missing following property definitions: ${unmatched.keys}")
        }
        return errors
    }

    protected open fun validateProperty(doc: Property, source: Property): List<String> {
        val errors: Errors = mutableListOf()
        fun addError(error: String) = errors.add("In property ${source.name}: $error")

        validateSchema(doc.schema, source.schema).forEach {
            addError(it)
        }

        return errors
    }

    protected open fun validateRequestBody(doc: RequestBody, source: RequestBody): List<String> {
        val errors: Errors = mutableListOf()
        fun addError(error: String) = errors.add("In requestBody: $error")

        if (doc.required != source.required) {
            addError("Doc's required doesn't match source's: ${doc.required} != ${source.required}")
        }

        val unmatchedContents = doc.content.map { it.contentType }.toMutableSet()
        source.content.forEach { content ->
            val contentDoc = doc.content.find { it.contentType == content.contentType }
            if (contentDoc == null) {
                addError("Cannot find doc for content: ${content.contentType}")
                return@forEach
            }
            unmatchedContents.remove(contentDoc.contentType)

            validateMediaType(contentDoc, content).forEach {
                addError(it)
            }
        }

        if (unmatchedContents.isNotEmpty()) {
            addError("There are unknown contents in the doc: $unmatchedContents")
        }

        return errors
    }
}
