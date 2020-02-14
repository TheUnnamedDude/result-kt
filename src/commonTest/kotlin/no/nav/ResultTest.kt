package no.nav

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

internal class ResultTest {
    private class TestException : Exception()

    @Test
    fun `unwrapping an error object should result in an exception`() {
        assertFailsWith<UnwrapException> {
            Result.Error<Any, TestException>(TestException()).unwrap()
        }
    }

    @Test
    fun `unwrapping an ok object should return the containing value`() {
        assertEquals("OK", Result.Ok<String, Throwable>("OK").unwrap())
    }

    @Test
    fun `mapping a ok value results a mapped result`() {
        val result = Result.Ok<String, TestException>("I am not ok")
            .map { it.replace(" not", "") }
        assertEquals(Result.Ok("I am ok"), result)
    }

    @Test
    fun `mapping a error value should return a mapped result`() {
        val testException = TestException()
        val result = Result.Error<Any, Throwable>(Throwable(testException))
            .mapError { assertNotNull(it.cause) }

        assertEquals(Result.Error<Any, Throwable>(testException), result)
    }

    @Test
    fun `wrapping exceptions as results`() {
        val testException = TestException()
        val result = wrapExceptions {
            throw testException
        }

        assertEquals(Result.Error<Nothing, Exception>(testException), result)
    }
}