package korrit.kotlin.openapi

import com.koriit.kotlin.slf4j.logger
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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.lang.reflect.Field
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

internal class OpenAPIMatcherTest {

    private val log = logger {}

    private val model = listOf(
        // Components::class is only important when resolving references
        Header::class,
        MediaType::class,
        OpenAPI::class,
        Operation::class,
        Parameter::class,
        Path::class,
        Property::class,
        RequestBody::class,
        Response::class,
        Schema::class
    )

    @Test
    fun `Should have matcher for all model objects`() {
        val funcs = OpenAPIMatcher::class.memberFunctions.map {
            it.parameters
                .filter { it.kind != KParameter.Kind.INSTANCE }
                .map { it.type.classifier }
        }

        for (obj in model) {
            if (obj == Components::class) continue
            val matcher = funcs.find {
                it.size == 2 && it[0] == obj && it[1] == obj
            }
            assertTrue(matcher != null, "$obj has no matcher function")
        }
    }

    @Test
    fun `Should detect changes of all relevant fields`() {
        val spec = newOpenAPI()
        assertTrue(spec.checkErrors().isEmpty())

        // Those are compatibility irrelevant and discrepancies should not be considered an error
        val exceptions = listOf(
            Response::description.javaField!!,
            Parameter::description.javaField!!,
            Schema::title.javaField!!
        )

        verify(spec, spec, exceptions)
    }

    private fun verify(spec: OpenAPI, obj: Any, exceptions: List<Field>) {
        for (prop in obj::class.memberProperties) {
            val field = prop.javaField!!
            field.isAccessible = true
            val value = field.get(obj) ?: continue

            log.info("Checking: ${obj::class.simpleName}::${field.name}")

            if (field in exceptions) {
                log.info("Skipping exception...")
                continue
            }

            if (value is String) {
                field.set(obj, "other $value")
                assertTrue(spec.checkErrors().isNotEmpty())
                field.set(obj, value)
            } else if (value is Boolean) {
                field.set(obj, !value)
                assertTrue(spec.checkErrors().isNotEmpty())
                field.set(obj, value)
            } else if (value::class in model) {
                verify(spec, value, exceptions)
            } else if (value is List<*>) {
                val elem = value[0]!!

                if (elem::class in model) {
                    verify(spec, elem, exceptions)
                } else if (elem !is String) {
                    throw AssertionError("Don't know how to verify list of '${elem::class.simpleName}'")
                }

                field.set(obj, listOf<String>())
                assertTrue(spec.checkErrors().isNotEmpty())
                field.set(obj, value)
            } else {
                throw AssertionError("Don't know how to verify '${value::class.simpleName}'")
            }
        }
    }

    private fun OpenAPI.checkErrors() = OpenAPIMatcher().match(this, newOpenAPI())

    @Suppress("LongMethod")
    internal fun newOpenAPI() = OpenAPI(
        version = "string",
        paths = listOf(
            Path(
                path = "string",
                operations = listOf(
                    Operation(
                        method = "string",
                        responses = listOf(
                            Response(
                                code = "string",
                                content = listOf(
                                    MediaType(
                                        contentType = "string",
                                        schema = emptySchema()
                                    )
                                ),
                                description = "string",
                                headers = listOf(
                                    Header(
                                        name = "string",
                                        required = true,
                                        deprecated = true,
                                        schema = emptySchema()
                                    )
                                )
                            )
                        ),
                        requestBody = RequestBody(
                            content = listOf(
                                MediaType(
                                    contentType = "string",
                                    schema = Schema(
                                        title = "string",
                                        type = "string",
                                        deprecated = true,
                                        nullable = true,
                                        format = "string",
                                        default = "string",
                                        uniqueItems = true,
                                        required = listOf(
                                            "string"
                                        ),
                                        properties = listOf(
                                            Property(
                                                name = "string",
                                                schema = emptySchema()
                                            )
                                        ),
                                        additionalProperties = emptySchema(),
                                        items = emptySchema(),
                                        enum = listOf(
                                            "string"
                                        )
                                    )
                                )
                            ),
                            required = true
                        ),
                        parameters = listOf(
                            Parameter(
                                name = "string",
                                inside = "string",
                                required = true,
                                deprecated = true,
                                description = "string",
                                schema = emptySchema()
                            )
                        ),
                        deprecated = true
                    )
                )
            )
        ),
        components = null
    )

    private fun emptySchema(): Schema {
        return Schema(
            title = null,
            type = null,
            deprecated = false,
            nullable = false,
            format = null,
            default = null,
            uniqueItems = null,
            required = null,
            properties = null,
            additionalProperties = null,
            items = null,
            enum = null
        )
    }
}
