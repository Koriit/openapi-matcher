package com.korrit.kotlin.openapi

import com.koriit.kotlin.slf4j.logger
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.lang.Exception

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
            } catch (e: Exception) {
                log.info("\n{}", spec)
                throw e
            }
        }

        val spec = OpenAPIMatcherTest().newOpenAPI()
        val spec2 = OpenAPIReader().load(spec.toString())
        assertTrue(OpenAPIMatcher().match(spec, spec2).isEmpty())
    }
}
