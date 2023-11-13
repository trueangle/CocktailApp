package io.github.trueangle.cocktail.util

import com.github.kittinunf.result.Result
import com.github.kittinunf.result.mapError
import io.github.trueangle.cocktail.domain.model.RequestException
import retrofit2.HttpException
import java.io.IOException

suspend inline fun <reified T> resultBodyOf(block: () -> T): Result<T, RequestException> =
    Result.of<T, Throwable> {
        block()
    }.mapError {
        it.toRequestException()
    }

suspend fun Throwable.toRequestException() = when (this) {
    is IOException -> RequestException.Network(cause = this)
    is HttpException -> RequestException.HttpException(code = this.code(), this)
    else -> RequestException.Unknown(cause = this)
}