package fr.delcey.logino.domain.home

import fr.delcey.logino.domain.home.model.HomeEntity
import fr.delcey.logino.domain.utils.HttpResult

interface HomeRepository {
    suspend fun getHomes(): HttpResult<List<HomeEntity>>
    suspend fun getHome(id: Long, forceRefresh: Boolean): HttpResult<HomeEntity>
}