package fr.delcey.logino.data.home

import java.math.BigDecimal
import androidx.collection.LruCache
import fr.delcey.logino.data.home.model.ListingDto
import fr.delcey.logino.data.home.model.ListingsDto
import fr.delcey.logino.data.utils.safeHttpCall
import fr.delcey.logino.domain.home.HomeRepository
import fr.delcey.logino.domain.home.model.HomeEntity
import fr.delcey.logino.domain.utils.HttpResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryRetrofit @Inject constructor(
    private val listingsDataSource: ListingsDataSource,
) : HomeRepository {

    private val lruCache: LruCache<Long, HomeEntity> = LruCache(50)

    override suspend fun getHomes(): HttpResult<List<HomeEntity>> = safeHttpCall {
        val response: ListingsDto = listingsDataSource.getListings()

        HttpResult.Success(
            response.listingDtos.map { listingDto ->
                mapToHomeEntity(listingDto).also { homeEntity ->
                    lruCache.put(homeEntity.id, homeEntity)
                }
            }
        )
    }

    override suspend fun getHome(id: Long, forceRefresh: Boolean): HttpResult<HomeEntity> {
        if (!forceRefresh) {
            val existing = lruCache[id]
            if (existing != null) {
                return HttpResult.Success(existing)
            }
        }

        return safeHttpCall {
            val response: ListingDto = listingsDataSource.getListing(id)
            HttpResult.Success(
                mapToHomeEntity(response).also { homeEntity ->
                    lruCache.put(homeEntity.id, homeEntity)
                }
            )
        }
    }

    private fun mapToHomeEntity(listingDto: ListingDto) = HomeEntity(
        id = listingDto.id,
        area = BigDecimal(listingDto.area),
        city = listingDto.city,
        price = BigDecimal(listingDto.price),
        propertyType = listingDto.propertyType,
        photoUrl = listingDto.url,
        vendor = listingDto.professional,
        rooms = listingDto.rooms,
        bedrooms = listingDto.bedrooms,
    )
}