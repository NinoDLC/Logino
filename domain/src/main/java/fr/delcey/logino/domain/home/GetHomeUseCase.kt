package fr.delcey.logino.domain.home

import fr.delcey.logino.domain.home.model.HomeEntity
import fr.delcey.logino.domain.utils.HttpResult
import javax.inject.Inject

class GetHomeUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
) {
    suspend fun invoke(id: Long, forceRefresh: Boolean): HttpResult<HomeEntity> = homeRepository.getHome(id, forceRefresh)
}