package fr.delcey.logino.data.home

import com.squareup.moshi.JsonDataException
import fr.delcey.logino.data.home.model.ListingDto
import fr.delcey.logino.data.home.model.ListingsDto
import fr.delcey.logino.data.utils.TestCoroutineRule
import fr.delcey.logino.domain.home.model.HomeEntity
import fr.delcey.logino.domain.utils.HttpResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.math.BigDecimal
import javax.net.ssl.SSLException

class HomeRepositoryRetrofitTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val listingsDataSource: ListingsDataSource = mockk()

    private lateinit var homeRepositoryRetrofit: HomeRepositoryRetrofit

    @Before
    fun setUp() {
        coEvery { listingsDataSource.getListings() } returns getDefaultListingsDto()
        coEvery { listingsDataSource.getListing(id = any()) } answers {
            getDefaultListingDto(index = firstArg<Long>().toInt())
        }

        homeRepositoryRetrofit = HomeRepositoryRetrofit(
            listingsDataSource
        )
    }

    // region getHomes
    @Test
    fun `nominal case - getHomes`() = testCoroutineRule.runTest {
        // When
        val result = homeRepositoryRetrofit.getHomes()

        // Then
        assertEquals(
            getDefaultHttpResultListHomeEntity(),
            result
        )
        coVerify(exactly = 1) {
            listingsDataSource.getListings()
        }
        confirmVerified(listingsDataSource)
    }

    @Test
    fun `edge case - getHomes without connectivity`() = testCoroutineRule.runTest {
        // Given
        coEvery { listingsDataSource.getListings() } throws IOException("")

        // When
        val result = homeRepositoryRetrofit.getHomes()

        // Then
        assertEquals(
            HttpResult.Failure(isFromUserConnectivity = true),
            result
        )
        coVerify(exactly = 1) {
            listingsDataSource.getListings()
        }
        confirmVerified(listingsDataSource)
    }

    @Test
    fun `edge case - getHomes with obsolete certificates`() = testCoroutineRule.runTest {
        // Given
        coEvery { listingsDataSource.getListings() } throws SSLException("")

        // When
        val result = homeRepositoryRetrofit.getHomes()

        // Then
        assertEquals(
            HttpResult.Failure(isFromUserConnectivity = true),
            result
        )
        coVerify(exactly = 1) {
            listingsDataSource.getListings()
        }
        confirmVerified(listingsDataSource)
    }

    @Test
    fun `edge case - getHomes with parsing error`() = testCoroutineRule.runTest {
        // Given
        coEvery { listingsDataSource.getListings() } throws JsonDataException()

        // When
        val result = homeRepositoryRetrofit.getHomes()

        // Then
        assertEquals(
            HttpResult.Failure(isFromUserConnectivity = false),
            result
        )
        coVerify(exactly = 1) {
            listingsDataSource.getListings()
        }
        confirmVerified(listingsDataSource)
    }

    @Test
    fun `error case - getHomes has listing area equal to 0`() = testCoroutineRule.runTest {
        // Given
        val listingsDto = getDefaultListingsDto().copy(
            listingDtos = getDefaultListingsDto().listingDtos.map { listingDto ->
                listingDto.copy(area = 0.0)
            }
        )
        coEvery { listingsDataSource.getListings() } returns listingsDto

        // When
        val result = homeRepositoryRetrofit.getHomes()

        // Then
        assertEquals(
            HttpResult.Failure(isFromUserConnectivity = false),
            result
        )
        coVerify(exactly = 1) {
            listingsDataSource.getListings()
        }
        confirmVerified(listingsDataSource)
    }
    // endregion getHomes


    // region getHome
    @Test
    fun `nominal case - getHome`() = testCoroutineRule.runTest {
        // When
        val result = homeRepositoryRetrofit.getHome(id = 1, forceRefresh = false)

        // Then
        assertEquals(
            HttpResult.Success(getDefaultHomeEntity(index = 1)),
            result
        )
        coVerify(exactly = 1) {
            listingsDataSource.getListing(id = 1)
        }
        confirmVerified(listingsDataSource)
    }

    @Test
    fun `edge case - getHome without connectivity`() = testCoroutineRule.runTest {
        // Given
        coEvery { listingsDataSource.getListing(any()) } throws IOException("")

        // When
        val result = homeRepositoryRetrofit.getHome(id = 1, forceRefresh = false)

        // Then
        assertEquals(
            HttpResult.Failure(isFromUserConnectivity = true),
            result
        )
        coVerify(exactly = 1) {
            listingsDataSource.getListing(id = 1)
        }
        confirmVerified(listingsDataSource)
    }

    @Test
    fun `edge case - getHome with obsolete certificates`() = testCoroutineRule.runTest {
        // Given
        coEvery { listingsDataSource.getListing(any()) } throws SSLException("")

        // When
        val result = homeRepositoryRetrofit.getHome(id = 1, forceRefresh = false)

        // Then
        assertEquals(
            HttpResult.Failure(isFromUserConnectivity = true),
            result
        )
        coVerify(exactly = 1) {
            listingsDataSource.getListing(id = 1)
        }
        confirmVerified(listingsDataSource)
    }

    @Test
    fun `edge case - getHome with parsing error`() = testCoroutineRule.runTest {
        // Given
        coEvery { listingsDataSource.getListing(any()) } throws JsonDataException()

        // When
        val result = homeRepositoryRetrofit.getHome(id = 1, forceRefresh = false)

        // Then
        assertEquals(
            HttpResult.Failure(isFromUserConnectivity = false),
            result
        )
        coVerify(exactly = 1) {
            listingsDataSource.getListing(id = 1)
        }
        confirmVerified(listingsDataSource)
    }

    @Test
    fun `edge case - getHome with warm cache from getHomes`() = testCoroutineRule.runTest {
        // Given
        homeRepositoryRetrofit.getHomes()

        // When
        val result = homeRepositoryRetrofit.getHome(id = 1, forceRefresh = false)

        // Then
        assertEquals(
            HttpResult.Success(getDefaultHomeEntity(index = 1)),
            result
        )
        coVerify(exactly = 0) {
            listingsDataSource.getListing(id = any())
        }
    }

    @Test
    fun `edge case - getHome with warm cache from getHome with the same id`() = testCoroutineRule.runTest {
        // Given
        homeRepositoryRetrofit.getHome(id = 1, forceRefresh = false)

        // When
        val result = homeRepositoryRetrofit.getHome(id = 1, forceRefresh = false)

        // Then
        assertEquals(
            HttpResult.Success(getDefaultHomeEntity(index = 1)),
            result
        )
        coVerify(exactly = 1) {
            listingsDataSource.getListing(id = 1)
        }
        confirmVerified(listingsDataSource)
    }

    @Test
    fun `edge case - getHome should still call API even with warm cache if forceRefresh is true`() = testCoroutineRule.runTest {
        // Given
        homeRepositoryRetrofit.getHome(id = 1, forceRefresh = false)

        // When
        homeRepositoryRetrofit.getHome(id = 1, forceRefresh = true)

        // Then
        coVerify(exactly = 2) {
            listingsDataSource.getListing(id = 1)
        }
        confirmVerified(listingsDataSource)
    }

    @Test
    fun `error case - getHome has listing area equal to 0`() = testCoroutineRule.runTest {
        // Given
        coEvery { listingsDataSource.getListing(id = 1) } returns getDefaultListingDto(index = 1).copy(
            area = 0.0
        )

        // When
        val result = homeRepositoryRetrofit.getHome(id = 1, forceRefresh = false)

        // Then
        assertEquals(
            HttpResult.Failure(isFromUserConnectivity = false),
            result
        )
        coVerify(exactly = 1) {
            listingsDataSource.getListing(id = 1)
        }
        confirmVerified(listingsDataSource)
    }

    // endregion getHome

    // region IN
    private fun getDefaultListingsDto() = ListingsDto(
        totalCount = 3,
        listingDtos = List(3) { index ->
            getDefaultListingDto(index)
        },
    )

    private fun getDefaultListingDto(index: Int) = ListingDto(
        id = index.toLong(),
        area = (index + 1).toDouble(),
        offerType = index,
        city = "city$index",
        price = index.toDouble(),
        propertyType = "propertyType$index",
        url = "photoUrl$index",
        professional = "vendor$index",
        rooms = index,
        bedrooms = index,
    )
    // endregion IN

    // region OUT
    private fun getDefaultHttpResultListHomeEntity(): HttpResult.Success<List<HomeEntity>> = HttpResult.Success(
        List(3) { index ->
            getDefaultHomeEntity(index)
        }
    )

    private fun getDefaultHomeEntity(index: Int) = HomeEntity(
        id = index.toLong(),
        area = BigDecimal(index + 1),
        city = "city$index",
        price = BigDecimal(index),
        propertyType = "propertyType$index",
        photoUrl = "photoUrl$index",
        vendor = "vendor$index",
        rooms = index,
        bedrooms = index,
    )
    // endregion OUT
}