package fr.delcey.logino.ui.home_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.delcey.logino.domain.home.GetHomeUseCase
import fr.delcey.logino.domain.home.model.HomeEntity
import fr.delcey.logino.domain.utils.HttpResult
import fr.delcey.logino.ui.R
import fr.delcey.logino.ui.home_detail.HomeDetailActivity.Companion.ARG_HOME_ID
import fr.delcey.logino.ui.mapper.HomeMapper
import fr.delcey.logino.ui.utils.NativeText
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class HomeDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getHomeUseCase: GetHomeUseCase,
    private val homeMapper: HomeMapper,
) : ViewModel() {

    private val refreshTriggerMutableSharedFlow = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val uiStateLiveData: LiveData<HomeDetailUiState> = liveData {
        val homeId: Long = requireNotNull(savedStateHandle[ARG_HOME_ID]) { "Use HomeDetailActivity.navigate()" }

        // Try to emit instantly a cached Home for a smooth SharedElementTransition
        emit(
            mapToHomeDetail(
                getHomeUseCase.invoke(id = homeId, forceRefresh = false)
            )
        )

        refreshTriggerMutableSharedFlow.collectLatest {
            emit(HomeDetailUiState.Loading)

            emit(
                mapToHomeDetail(
                    getHomeUseCase.invoke(id = homeId, forceRefresh = true)
                )
            )
        }
    }

    fun onPullToRefresh() {
        refreshTriggerMutableSharedFlow.tryEmit(Unit)
    }

    private fun mapToHomeDetail(homeResult: HttpResult<HomeEntity>): HomeDetailUiState = when (homeResult) {
        is HttpResult.Failure -> HomeDetailUiState.Error(
            if (homeResult.isFromUserConnectivity) {
                NativeText.Resource(R.string.error_connectivity)
            } else {
                NativeText.Resource(R.string.error_generic)
            }
        )

        is HttpResult.Success -> HomeDetailUiState.Content(
            photoUrl = homeResult.data.photoUrl,
            vendor = NativeText.Simple(homeResult.data.vendor),
            price = homeMapper.getHomePrice(homeResult.data.price),
            pricePerSquareMeter = homeMapper.getHomePricePerSquareMeter(homeResult.data.price, homeResult.data.area),
            propertyType = NativeText.Simple(homeResult.data.propertyType),
            roomsAndSize = homeMapper.getRoomsAndSize(
                rooms = homeResult.data.rooms,
                bedrooms = homeResult.data.bedrooms,
                area = homeResult.data.area,
            ),
            city = NativeText.Simple(homeResult.data.city),
        )
    }
}
