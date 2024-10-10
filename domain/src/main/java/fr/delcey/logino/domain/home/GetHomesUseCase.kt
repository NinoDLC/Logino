package fr.delcey.logino.domain.home

import fr.delcey.logino.domain.home.model.HomeEntity
import fr.delcey.logino.domain.utils.HttpResult
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class GetHomesUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
) {
    companion object {
        private const val MAX_RETRIES = 3
    }

    suspend fun invoke(): HttpResult<List<HomeEntity>> = doAndRetryCall()

    private suspend fun doAndRetryCall(retryCount: Int = 0): HttpResult<List<HomeEntity>> =
        when (val result = homeRepository.getHomes()) {
            is HttpResult.Failure -> {
                if (result.isFromUserConnectivity && retryCount < MAX_RETRIES) {
                    delay(1.seconds * retryCount)
                    doAndRetryCall(retryCount + 1)
                } else {
                    result
                }
            }

            is HttpResult.Success -> result
        }
}