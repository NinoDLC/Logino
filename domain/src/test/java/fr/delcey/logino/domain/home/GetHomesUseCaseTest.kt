package fr.delcey.logino.domain.home

import fr.delcey.logino.domain.home.model.HomeEntity
import fr.delcey.logino.domain.utils.HttpResult
import fr.delcey.logino.domain.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.currentTime
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class GetHomesUseCaseTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val homeRepository: HomeRepository = mockk()

    private lateinit var getHomeUseCase: GetHomesUseCase

    @Before
    fun setUp() {
        coEvery { homeRepository.getHomes() } returns getDefaultHttpResultListHomeEntity()

        getHomeUseCase = GetHomesUseCase(
            homeRepository
        )
    }

    @Test
    fun `nominal case`() = testCoroutineRule.runTest {
        // When
        val result = getHomeUseCase.invoke()

        // Then
        assertEquals(
            getDefaultHttpResultListHomeEntity(),
            result
        )
        coVerify(exactly = 1) {
            homeRepository.getHomes()
        }
        confirmVerified(homeRepository)
    }

    @Test
    fun `edge case - retry instantly if user has bad connectivity`() = testCoroutineRule.runTest {
        // Given
        coEvery { homeRepository.getHomes() } returns
            HttpResult.Failure(isFromUserConnectivity = true) andThen
            getDefaultHttpResultListHomeEntity()

        // When
        val result = getHomeUseCase.invoke()

        // Then
        assertEquals(
            getDefaultHttpResultListHomeEntity(),
            result
        )
        assertEquals(
            0,
            currentTime
        )
        coVerify(exactly = 2) {
            homeRepository.getHomes()
        }
        confirmVerified(homeRepository)
    }

    @Test
    fun `edge case - retry twice and wait between if user has bad connectivity`() = testCoroutineRule.runTest {
        // Given
        coEvery { homeRepository.getHomes() } returns
            HttpResult.Failure(isFromUserConnectivity = true) andThen
            HttpResult.Failure(isFromUserConnectivity = true) andThen
            getDefaultHttpResultListHomeEntity()

        // When
        val result = getHomeUseCase.invoke()

        // Then
        assertEquals(
            getDefaultHttpResultListHomeEntity(),
            result
        )
        assertEquals(
            1_000,
            currentTime
        )
        coVerify(exactly = 3) {
            homeRepository.getHomes()
        }
        confirmVerified(homeRepository)
    }

    @Test
    fun `edge case - retry thrice and wait between if user has bad connectivity`() = testCoroutineRule.runTest {
        // Given
        coEvery { homeRepository.getHomes() } returns
            HttpResult.Failure(isFromUserConnectivity = true) andThen
            HttpResult.Failure(isFromUserConnectivity = true) andThen
            HttpResult.Failure(isFromUserConnectivity = true) andThen
            getDefaultHttpResultListHomeEntity()

        // When
        val result = getHomeUseCase.invoke()

        // Then
        assertEquals(
            getDefaultHttpResultListHomeEntity(),
            result
        )
        assertEquals(
            3_000,
            currentTime
        )
        coVerify(exactly = 4) {
            homeRepository.getHomes()
        }
        confirmVerified(homeRepository)
    }

    @Test
    fun `edge case - retry no more than 3 times if user has bad connectivity`() = testCoroutineRule.runTest {
        // Given
        coEvery { homeRepository.getHomes() } returns HttpResult.Failure(isFromUserConnectivity = true)

        // When
        val result = getHomeUseCase.invoke()

        // Then
        assertEquals(
            HttpResult.Failure(isFromUserConnectivity = true),
            result
        )
        assertEquals(
            3_000,
            currentTime
        )
        coVerify(exactly = 4) {
            homeRepository.getHomes()
        }
        confirmVerified(homeRepository)
    }

    @Test
    fun `edge case - don't retry if a generic error occurred`() = testCoroutineRule.runTest {
        // Given
        coEvery { homeRepository.getHomes() } returns HttpResult.Failure(isFromUserConnectivity = false)

        // When
        val result = getHomeUseCase.invoke()

        // Then
        assertEquals(
            HttpResult.Failure(isFromUserConnectivity = false),
            result
        )
        assertEquals(
            0,
            currentTime
        )
        coVerify(exactly = 1) {
            homeRepository.getHomes()
        }
        confirmVerified(homeRepository)
    }

    // region Fixture
    private fun getDefaultHttpResultListHomeEntity(): HttpResult.Success<List<HomeEntity>> = HttpResult.Success(
        List(3) { index ->
            HomeEntity(
                id = index.toLong(),
                area = BigDecimal(index),
                city = "city$index",
                price = BigDecimal(index),
                propertyType = "propertyType$index",
                photoUrl = "photoUrl$index",
                vendor = "vendor$index",
                rooms = index,
                bedrooms = index,
            )
        }
    )
    // endregion Fixture
}