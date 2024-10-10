package fr.delcey.logino.data.home.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ListingsDto(

    @Json(name = "totalCount")
    val totalCount: Int,

    @Json(name = "items")
    val listingDtos: List<ListingDto>,
)