package fr.delcey.logino.domain.home

import fr.delcey.logino.domain.home.model.HomeEntity
import fr.delcey.logino.domain.utils.HttpResult
import fr.delcey.logino.domain.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetHomeUseCaseTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val homeRepository: HomeRepository = mockk()

    private lateinit var getHomeUseCase: GetHomeUseCase

    @Before
    fun setUp() {
        getHomeUseCase = GetHomeUseCase(
            homeRepository
        )
    }

    @Test
    fun `verify invoke`() = testCoroutineRule.runTest {
        // Given
        val mockedResult: HttpResult<HomeEntity> = mockk()
        coEvery { homeRepository.getHome(any(), any()) } returns mockedResult

        // When
        val result = getHomeUseCase.invoke(7, false)

        // Then
        assertEquals(mockedResult, result)
        coVerify(exactly = 1) {
            homeRepository.getHome(7, false)
        }
        confirmVerified(homeRepository)
    }
}