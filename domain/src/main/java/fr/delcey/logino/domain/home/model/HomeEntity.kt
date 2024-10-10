package fr.delcey.logino.domain.home.model

import android.icu.math.BigDecimal

data class HomeEntity(
    val id: Long,
    val area: BigDecimal,
    val city: String,
    val price: BigDecimal,
    val propertyType: String,
    val photoUrl: String?,
    val vendor: String,
    val rooms: Int?,
    val bedrooms: Int?,
)
