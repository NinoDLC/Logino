package fr.delcey.logino.data.utils

import fr.delcey.logino.domain.utils.HttpResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.IOException

suspend fun <T> safeHttpCall(block: suspend () -> HttpResult<T>): HttpResult<T> = withContext(Dispatchers.IO) {
    try {
        block()
    } catch (e: Exception) {
        coroutineContext.ensureActive()
        e.printStackTrace()
        HttpResult.Failure(isFromUserConnectivity = e is IOException)
    }
}