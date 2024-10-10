package fr.delcey.logino.ui.mapper

import fr.delcey.logino.ui.R
import fr.delcey.logino.ui.utils.NativeText
import java.math.BigDecimal
import java.text.NumberFormat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeMapper @Inject constructor(private val numberFormat: NumberFormat) {

    fun getHomePrice(homePrice: BigDecimal) = NativeText.Simple(
        text = numberFormat.format(homePrice)
    )

    fun getHomePricePerSquareMeter(homePrice: BigDecimal, area: BigDecimal): NativeText = NativeText.Argument(
        id = R.string.home_size_per_square_meter,
        arg = numberFormat.format(homePrice.divide(area))
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