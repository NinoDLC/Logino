package fr.delcey.logino.domain.utils

sealed class HttpResult<out T> {
    data class Success<T>(val data: T) : HttpResult<T>()
    data class Failure(val isFromUserConnectivity: Boolean) : HttpResult<Nothing>()
}