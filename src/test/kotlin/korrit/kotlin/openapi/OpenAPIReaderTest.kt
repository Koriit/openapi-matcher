package korrit.kotlin.openapi

import org.junit.jupiter.api.Test

internal class OpenAPIReaderTest {

    @Test
    fun testReading() {
        val reader = OpenAPIReader()

        mapOf(
            "petstore" to "/petstore.yaml",
            "petstore-expanded" to "/petstore-expanded.yaml",
            "uspto" to "/uspto.yaml",
            "api-with-examples" to "/api-with-examples.yaml",
            "link-example" to "/link-example.yaml",
            "callback-example" to "/callback-example.yaml"
        ).testCases {
            reader.load({}.javaClass.getResourceAsStream(this))
        }
    }
}