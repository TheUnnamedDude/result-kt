package no.nav

sealed class Result<V, E: Throwable> {
    abstract fun unwrap(): V
    abstract fun expect(message: String): V
    abstract fun <T> map(mapper: (V) -> T): Result<T, E>
    abstract fun <T:  Throwable> mapError(mapper: (E) -> T): Result<V, T>
    abstract val isError: Boolean
    abstract val isOk: Boolean

    data class Ok<V, E: Throwable>(val value: V) : Result<V, E>() {
        override fun unwrap() = value
        override fun expect(message: String) = value
        override fun <T> map(mapper: (V) -> T): Result<T, E> = Ok(mapper(value))
        override fun <T: Throwable> mapError(mapper: (E) -> T): Result<V, T> = Ok(value)
        override val isError = false
        override val isOk = true
    }

    data class Error<V, E: Throwable>(val error: E) : Result<V, E>() {
        override fun unwrap(): V = throw UnwrapException("Called unwrap on an error object", error)
        override fun expect(message: String) = throw UnwrapException(message, error)
        override fun <T> map(mapper: (V) -> T): Result<T, E> = Error(error)
        override fun <T : Throwable> mapError(mapper: (E) -> T): Result<V, T> = Error(mapper(error))
        override val isError = true
        override val isOk = false
    }
}

fun <T> wrapExceptions(method: () -> T): Result<T, Exception> = try {
    Result.Ok(method())
} catch (e: Exception) {
    Result.Error(e)
}

class UnwrapException(message: String, cause: Throwable) : Throwable(message, cause)
