package korrit.kotlin.openapi

import koriit.kotlin.slf4j.logger

private val log = logger {}

internal fun <T> List<T>.testCases(test: T.() -> Unit) {
    withIndex().forEach { (index, case) ->
        try {
            log.info("Testing case ${index + 1} - $case")
            test(case)
        } catch (e: Throwable) {
            throw AssertionError("Case ${index + 1} failed - $case", e)
        }
    }
}

internal fun <T> Map<String, T>.testCases(test: T.() -> Unit) {
    forEach { (name, case) ->
        try {
            log.info("Testing case '$name' - $case")
            test(case)
        } catch (e: Throwable) {
            throw AssertionError("Case '$name' failed - $case", e)
        }
    }
}
