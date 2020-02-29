package korrit.kotlin.openapi

import koriit.kotlin.slf4j.logger
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
import java.lang.Exception
import java.lang.RuntimeException
import java.lang.reflect.Field
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

internal class ModelTest {

    private val log = logger {}

    @Test
    fun `Should write equivalent YAML spec`() {
        mapOf(
            "petstore" to "/examples/petstore.yaml",
            "petstore-expanded" to "/examples/petstore-expanded.yaml",
            "uspto" to "/examples/uspto.yaml",
            "api-with-examples" to "/examples/api-with-examples.yaml",
            "link-example" to "/examples/link-example.yaml",
            "callback-example" to "/examples/callback-example.yaml",
            "headers" to "/headers.yaml",
            "unknown-method" to "/unknown-method.yaml"
        ).testCases {
            val spec = OpenAPIReader().load({}.javaClass.getResourceAsStream(this))

            try {
                val spec2 = OpenAPIReader().load(spec.toString())
                assertTrue(OpenAPIMatcher().match(spec, spec2).isEmpty())
            } catch(e: Exception) {
                log.info("\n{}", spec)
                throw e
            }
        }

        val spec = OpenAPIMatcherTest().newOpenAPI()
        val spec2 = OpenAPIReader().load(spec.toString())
        assertTrue(OpenAPIMatcher().match(spec, spec2).isEmpty())
    }
}
