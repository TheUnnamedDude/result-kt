package no.nav

class ErrorScope {
    fun <V, E: Exception> doTry(result: Result<V, E>): V = when (result) {
        is Result.Error -> throw result.error
        is Result.Ok -> result.value
    }
}

inline fun <T, reified E: Throwable> errorScoped(scope: ErrorScope.() -> T): Result<T, E> {
    val errorScope = ErrorScope()
    return try {
        Result.Ok(errorScope.scope())
    } catch (e: Exception) {
        if (e is E) {
            Result.Error(e)
        } else {
            throw e
        }
    }
}