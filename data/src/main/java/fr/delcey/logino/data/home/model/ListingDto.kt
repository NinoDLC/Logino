package fr.delcey.logino.data.home.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ListingDto(

    @Json(name = "id")
    val id: Long,

    @Json(name = "area")
    val area: Double,

    @Json(name = "offerType")
    val offerType: Int,

    @Json(name = "city")
    val city: String,

    @Json(name = "price")
    val price: Double,

    @Json(name = "propertyType")
    val propertyType: String,

    @Json(name = "url")
    val url: String?,

    @Json(name = "professional")
    val professional: String,

    @Json(name = "rooms")
    val rooms: Int?,

    @Json(name = "bedrooms")
    val bedrooms: Int?,
)