package fr.delcey.logino.ui.mapper

import android.icu.math.BigDecimal
import android.icu.text.NumberFormat
import fr.delcey.logino.ui.R
import fr.delcey.logino.ui.utils.NativeText
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeMapper @Inject constructor() {
    fun getHomePrice(homePrice: BigDecimal) = NativeText.Simple(
        text = NumberFormat.getCurrencyInstance().apply {
            maximumFractionDigits = 0
        }.format(homePrice)
    )

    fun getHomePricePerSquareMeter(price: BigDecimal, area: BigDecimal): NativeText = NativeText.Argument(
        id = R.string.home_size_per_square_meter,
        arg = NumberFormat.getCurrencyInstance().apply {
            maximumFractionDigits = 0
        }.format(price.divide(area))
    )

    fun getRoomsAndSize(
        rooms: Int?,
        bedrooms: Int?,
        area: BigDecimal,
    ): NativeText {
        val roomsNativeText = rooms?.let {
            NativeText.Plural(
                id = R.plurals.rooms,
                number = rooms,
                args = listOf(rooms)
            )
        }
        val bedroomsNativeText = bedrooms?.let {
            NativeText.Plural(
                id = R.plurals.bedrooms,
                number = bedrooms,
                args = listOf(bedrooms)
            )
        }

        return NativeText.Multi(
            listOfNotNull(
                roomsNativeText,
                bedroomsNativeText,
                getNativeTextHomeSize(area)
            )
        )
    }

    private fun getNativeTextHomeSize(area: BigDecimal) = NativeText.Argument(
        id = R.string.home_size,
        arg = area.toInt()
    )
}