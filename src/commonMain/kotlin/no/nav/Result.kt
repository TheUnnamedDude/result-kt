package no.nav

sealed class Result<V, E> {
    abstract fun unwrap(): V
    abstract fun expect(message: String): V
    abstract fun unwrapOr(errorValue: V): V
    abstract fun unwrapOrElse(errorMapper: (E) -> V): V
    abstract fun <T> andThen(mapper: (V) -> Result<T, E>): Result<T, E>
    abstract fun <T> map(mapper: (V) -> T): Result<T, E>
    abstract fun <T:  Throwable> mapError(mapper: (E) -> T): Result<V, T>
    abstract val isError: Boolean
    abstract val isOk: Boolean

    data class Ok<V, E>(val value: V) : Result<V, E>() {
        override fun unwrap() = value
        override fun expect(message: String) = value
        override fun <T> andThen(mapper: (V) -> Result<T, E>): Result<T, E> = mapper(value)
        override fun <T> map(mapper: (V) -> T): Result<T, E> = Ok(mapper(value))
        override fun unwrapOr(errorValue: V) = value
        override fun unwrapOrElse(errorMapper: (E) -> V): V = value
        override fun <T: Throwable> mapError(mapper: (E) -> T): Result<V, T> = this as Ok<V, T>
        override val isError = false
        override val isOk = true
    }

    data class Error<V, E>(val error: E) : Result<V, E>() {
        override fun unwrap(): V = throw UnwrapException("Called unwrap on an error object", error)
        override fun expect(message: String) = throw UnwrapException(message, error)
        override fun <T> andThen(mapper: (V) -> Result<T, E>): Result<T, E> = this as Error<T, E>
        override fun <T> map(mapper: (V) -> T): Result<T, E> = this as Error<T, E>
        override fun unwrapOr(errorValue: V): V = errorValue
        override fun unwrapOrElse(errorMapper: (E) -> V): V = errorMapper(error)
        override fun <T : Throwable> mapError(mapper: (E) -> T): Result<V, T> = Error(mapper(error))
        override val isError = true
        override val isOk = false
    }
}

fun <V, T, E> List<Result<V, E>>.andThen(mapper: (V) -> Result<T, E>): List<Result<T, E>> = map { it.andThen(mapper) }

fun <T> wrapExceptions(method: () -> T): Result<T, Exception> = try {
    Result.Ok(method())
} catch (e: Exception) {
    Result.Error(e)
}

class UnwrapException(message: String, val error: Any?) : Throwable(message, error as? Throwable)
