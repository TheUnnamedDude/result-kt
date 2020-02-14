package no.nav

import kotlin.test.Test
import kotlin.test.assertEquals

internal class ErrorScopeTest {
    @Test
    fun `doTry returns an Error when trying on a Error result type`() {
        val exception = Exception()
        val result = errorScoped<String, Exception> {
            doTry(Result.Error(exception))
        }

        assertEquals(Result.Error(exception), result)
    }

    @Test
    fun `doTry returns Ok when trying on a Ok Result type`() {
        val string = "I am ok"
        val result = errorScoped<String, Exception> {
            doTry(Result.Ok<String, Exception>(string))
        }

        assertEquals(Result.Ok(string), result)
    }
}
