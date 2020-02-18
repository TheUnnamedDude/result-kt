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

    @Test
    fun `unwrapOr on a ok value returns the value`() {
        val result = Result.Ok<String, Exception>("OK")
        assertEquals("OK", result.unwrapOr("ERROR"))
    }

    @Test
    fun `unwrapOr on a error value returns the mapped error`() {
        val result = Result.Error<String, Exception>(TestException())
        assertEquals("OK", result.unwrapOr("OK"))
    }

    @Test
    fun `unwrapOrElse on a ok value returns the value`() {
        val result = Result.Ok<String, Exception>("OK")
        assertEquals("OK", result.unwrapOrElse { "ERROR" })
    }

    @Test
    fun `unwrapOrElse on a error value returns the mapped error`() {
        val result = Result.Error<String, Exception>(TestException())
        assertEquals("OK", result.unwrapOrElse { "OK" })
    }

    @Test
    fun `andThen on chained results where second block is an error results in a error`() {
        val exception = TestException()
        val result = Result.Ok<String, Exception>("Test")
            .andThen { Result.Error<String, Exception>(exception) }

        assertEquals(Result.Error<String, Exception>(exception), result)
    }

    @Test
    fun `andThen on a chained result where both results are ok results in a ok value`() {
        val result = Result.Ok<String, Exception>("Test")
            .andThen { Result.Ok<String, Exception>(it + "Test") }

        assertEquals(Result.Ok("TestTest"), result)
    }

    @Test
    fun `andThen on two errors result to the first error`() {
        val testException = TestException()
        val result = Result.Error<String, Exception>(testException)
            .andThen { Result.Error<String, Exception>(Exception()) }

        assertEquals(Result.Error<String, Exception>(testException), result)
    }

    @Test
    fun `andThen on a list`() {
        val results = listOf<Result<Int, Int>>(
            Result.Error(19),
            Result.Ok(4),
            Result.Ok(13),
            Result.Error(97)
        ).andThen(this::divideByTwo)
        val expected = listOf<Result<Int, Int>>(
            Result.Error(19),
            Result.Ok(2),
            Result.Error(13),
            Result.Error(97)
        )

        assertEquals(expected, results)
    }

    fun divideByTwo(value: Int): Result<Int, Int> {
        if (value % 2 != 0) return Result.Error(value)
        return Result.Ok(value / 2)
    }
}