package korrit.kotlin.openapi

import org.junit.jupiter.api.Test

internal class OpenAPIReaderTest {

    @Test
    fun testReading() {
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
            OpenAPIReader().load({}.javaClass.getResourceAsStream(this))
        }
    }
}
