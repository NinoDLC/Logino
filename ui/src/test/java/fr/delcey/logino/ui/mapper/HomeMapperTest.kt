package fr.delcey.logino.ui.mapper

import java.math.BigDecimal
import fr.delcey.logino.ui.R
import fr.delcey.logino.ui.utils.NativeText
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.NumberFormat

class HomeMapperTest {

    private val numberFormat: NumberFormat = mockk()
    private val homeMapper = HomeMapper(numberFormat)

    @Test
    fun `when getHomePrice is called with a value, then it calls the numberFormat`() {
        // Given
        val param = BigDecimal(100)
        val mockedResult = "100 €"
        every { numberFormat.format(param) } returns mockedResult

        // When
        val result = homeMapper.getHomePrice(param)

        // Then
        assertEquals(NativeText.Simple(mockedResult), result)
        verify(exactly = 1) {
            numberFormat.format(param)
        }
        confirmVerified(numberFormat)
    }

    @Test
    fun `when getHomePricePerSquareMeter is called with a value, then it calls the numberFormat with the divided value`() {
        // Given
        val homePrice = BigDecimal(100)
        val homeArea = BigDecimal(2)
        val pricePerArea = BigDecimal(50)
        val mockedResult = "50 €"
        every { numberFormat.format(pricePerArea) } returns mockedResult

        // When
        val result = homeMapper.getHomePricePerSquareMeter(homePrice, homeArea)

        // Then
        assertEquals(
            NativeText.Argument(
                id = R.string.home_size_per_square_meter,
                arg = mockedResult
            ),
            result
        )
        verify(exactly = 1) {
            numberFormat.format(pricePerArea)
        }
        confirmVerified(numberFormat)
    }

    @Test
    fun `when getRoomsAndSize is called with all parameters, then it returns the full text`() {
        // Given
        val rooms = 4
        val bedrooms = 2
        val area = BigDecimal(80)

        // When
        val result = homeMapper.getRoomsAndSize(rooms, bedrooms, area)

        // Then
        assertEquals(
            NativeText.Multi(
                listOf(
                    NativeText.Plural(
                        id = R.plurals.rooms,
                        number = rooms,
                        args = listOf(rooms)
                    ),
                    NativeText.Plural(
                        id = R.plurals.bedrooms,
                        number = bedrooms,
                        args = listOf(bedrooms)
                    ),
                    NativeText.Argument(
                        id = R.string.home_size,
                        arg = area.toInt()
                    )
                )
            ),
            result
        )
    }

    @Test
    fun `when getRoomsAndSize is called without rooms, then it returns a partial text`() {
        // Given
        val rooms = null
        val bedrooms = 3
        val area = BigDecimal(150)

        // When
        val result = homeMapper.getRoomsAndSize(rooms, bedrooms, area)

        // Then
        assertEquals(
            NativeText.Multi(
                listOf(
                    NativeText.Plural(
                        id = R.plurals.bedrooms,
                        number = bedrooms,
                        args = listOf(bedrooms)
                    ),
                    NativeText.Argument(
                        id = R.string.home_size,
                        arg = area.toInt()
                    )
                )
            ),
            result
        )
    }

    @Test
    fun `when getRoomsAndSize is called without bedrooms, then it returns a partial text`() {
        // Given
        val rooms = 5
        val bedrooms = null
        val area = BigDecimal(600)

        // When
        val result = homeMapper.getRoomsAndSize(rooms, bedrooms, area)

        // Then
        assertEquals(
            NativeText.Multi(
                listOf(
                    NativeText.Plural(
                        id = R.plurals.rooms,
                        number = rooms,
                        args = listOf(rooms)
                    ),
                    NativeText.Argument(
                        id = R.string.home_size,
                        arg = area.toInt()
                    )
                )
            ),
            result
        )
    }

    @Test
    fun `when getRoomsAndSize is called without rooms or bedrooms, then it returns the size only`() {
        // Given
        val rooms = null
        val bedrooms = null
        val area = BigDecimal(80)

        // When
        val result = homeMapper.getRoomsAndSize(rooms, bedrooms, area)

        // Then
        assertEquals(
            NativeText.Multi(
                listOf(
                    NativeText.Argument(
                        id = R.string.home_size,
                        arg = area.toInt()
                    )
                )
            ),
            result
        )
    }
}