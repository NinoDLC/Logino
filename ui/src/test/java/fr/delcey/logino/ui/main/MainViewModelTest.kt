package fr.delcey.logino.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import fr.delcey.logino.domain.home.GetHomesUseCase
import fr.delcey.logino.domain.home.model.HomeEntity
import fr.delcey.logino.domain.utils.HttpResult
import fr.delcey.logino.ui.R
import fr.delcey.logino.ui.mapper.HomeMapper
import fr.delcey.logino.ui.utils.EquatableCallbackWithParam
import fr.delcey.logino.ui.utils.NativeText
import fr.delcey.logino.ui.utils.TestCoroutineRule
import fr.delcey.logino.ui.utils.observeForTesting
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import kotlin.time.Duration.Companion.seconds

class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val getHomesUseCase: GetHomesUseCase = mockk()
    private val homeMapper: HomeMapper = mockk()

    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setUp() {
        coEvery { getHomesUseCase.invoke() } returns getDefaultHomeResult()
        every { homeMapper.getHomePrice(any()) } returns NativeText.Simple("price")
        every { homeMapper.getHomePricePerSquareMeter(any(), any()) } returns NativeText.Simple("pricePerSquareMeter")
        every { homeMapper.getRoomsAndSize(any(), any(), any()) } returns NativeText.Simple("roomsAndSize")

        mainViewModel = MainViewModel(
            getHomesUseCase,
            homeMapper
        )
    }

    @Test
    fun `nominal case`() = testCoroutineRule.runTest {
        // When
        mainViewModel.uiStateLiveData.observeForTesting(this) {
            // Then
            assertEquals(getDefaultMainUiState(), it.value)
            coVerify(exactly = 1) {
                getHomesUseCase.invoke()
                homeMapper.getHomePrice(BigDecimal(0))
                homeMapper.getHomePrice(BigDecimal(1))
                homeMapper.getHomePrice(BigDecimal(2))
                homeMapper.getHomePricePerSquareMeter(BigDecimal(0), BigDecimal(0))
                homeMapper.getHomePricePerSquareMeter(BigDecimal(1), BigDecimal(1))
                homeMapper.getHomePricePerSquareMeter(BigDecimal(2), BigDecimal(2))
                homeMapper.getRoomsAndSize(0, 0, BigDecimal(0))
                homeMapper.getRoomsAndSize(1, 1, BigDecimal(1))
                homeMapper.getRoomsAndSize(2, 2, BigDecimal(2))
            }
        }

        confirmVerified(getHomesUseCase, homeMapper)
    }

    @Test
    fun `edge case - internet is slow`() = testCoroutineRule.runTest {
        // Given
        coEvery { getHomesUseCase.invoke() } coAnswers {
            delay(1.seconds)
            getDefaultHomeResult()
        }

        // When
        mainViewModel.uiStateLiveData.observeForTesting(this) {
            // Then
            assertEquals(MainUiState.Loading, it.value)

            // When 2
            advanceTimeBy(1.seconds)
            runCurrent()

            // Then 2
            assertEquals(getDefaultMainUiState(), it.value)
            coVerify(exactly = 1) {
                getHomesUseCase.invoke()
            }
        }

        confirmVerified(getHomesUseCase)
    }

    @Test
    fun `edge case - when user pulls to refresh, a new list is fetched`() = testCoroutineRule.runTest {
        // Given
        coEvery { getHomesUseCase.invoke() } returns getDefaultHomeResult() andThen getDefaultHomeResult(itemCount = 4)

        mainViewModel.uiStateLiveData.observeForTesting(this) {
            // When
            mainViewModel.onPullToRefresh()
            runCurrent()

            // Then
            assertEquals(getDefaultMainUiState(itemCount = 4), it.value)
            coVerify(exactly = 2) {
                getHomesUseCase.invoke()
            }
        }

        confirmVerified(getHomesUseCase)
    }

    @Test
    fun `error case - not connected to internet`() = testCoroutineRule.runTest {
        // Given
        coEvery { getHomesUseCase.invoke() } returns HttpResult.Failure(isFromUserConnectivity = true)

        // When
        mainViewModel.uiStateLiveData.observeForTesting(this) {
            // Then
            assertEquals(
                MainUiState.Error(
                    NativeText.Resource(R.string.error_connectivity)
                ),
                it.value
            )
        }
    }

    @Test
    fun `error case - generic error`() = testCoroutineRule.runTest {
        // Given
        coEvery { getHomesUseCase.invoke() } returns HttpResult.Failure(isFromUserConnectivity = false)

        // When
        mainViewModel.uiStateLiveData.observeForTesting(this) {
            // Then
            assertEquals(
                MainUiState.Error(
                    NativeText.Resource(R.string.error_generic)
                ),
                it.value
            )
        }
    }

    // region IN
    private fun getDefaultHomeResult(itemCount: Int = 3): HttpResult<List<HomeEntity>> = HttpResult.Success(
        List(itemCount) { index ->
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
    // endregion IN

    // region OUT

    private fun getDefaultMainUiState(itemCount: Int = 3): MainUiState = MainUiState.Content(
        items = List(itemCount) { index ->
            getDefaultMainItemUiState(index)
        }
    )

    private fun getDefaultMainItemUiState(index: Int) = MainItemUiState(
        id = index.toLong(),
        photoUrl = "photoUrl$index",
        vendor = NativeText.Simple("vendor$index"),
        price = NativeText.Simple("price"),
        pricePerSquareMeter = NativeText.Simple("pricePerSquareMeter"),
        propertyType = NativeText.Simple("propertyType$index"),
        roomsAndSize = NativeText.Simple("roomsAndSize"),
        city = NativeText.Simple("city$index"),
        onClick = EquatableCallbackWithParam {},
    )
    // endregion OUT
}