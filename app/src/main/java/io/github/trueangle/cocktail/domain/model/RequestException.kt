package io.github.trueangle.cocktail.domain.model

sealed class RequestException : Throwable() {

    data class Network(override val cause: Throwable?) : RequestException()

    data class HttpException(val code: Int, override val cause: Throwable?) : RequestException()

    data class Unknown(override val cause: Throwable?) : RequestException()
}
