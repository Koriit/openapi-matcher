package korrit.kotlin.openapi

fun <T> List<T>.testCases(test: T.() -> Unit) {
    withIndex().forEach { (index, case) ->
        try {
            test(case)
        } catch (e: Throwable) {
            throw AssertionError("Case ${index + 1} failed", e)
        }
    }
}

fun <T> Map<String, T>.testCases(test: T.() -> Unit) {
    forEach { (name, case) ->
        try {
            test(case)
        } catch (e: Throwable) {
            throw AssertionError("Case '$name' failed", e)
        }
    }
}
