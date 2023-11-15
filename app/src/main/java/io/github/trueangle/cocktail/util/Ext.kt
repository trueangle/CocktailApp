package io.github.trueangle.cocktail.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.mapError
import io.github.trueangle.cocktail.domain.model.RequestException
import retrofit2.HttpException
import java.io.IOException
import java.net.URLDecoder
import java.net.URLEncoder

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

fun String.encodeUrl() = URLEncoder.encode(this, "utf-8")
fun String.decodeUrl() = URLDecoder.decode(this, "utf-8")

inline fun <V : ViewModel> viewModelFactory(crossinline creator: () -> V): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return creator() as T
        }
    }
}
