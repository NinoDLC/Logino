package fr.delcey.logino.data.home

import fr.delcey.logino.data.home.model.ListingDto
import fr.delcey.logino.data.home.model.ListingsDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ListingsDataSource {
    @GET("listings.json")
    suspend fun getListings(): ListingsDto

    @GET("listings/{id}.json")
    suspend fun getListing(@Path("id") id: Long): ListingDto
}