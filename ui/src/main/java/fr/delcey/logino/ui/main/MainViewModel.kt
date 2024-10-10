package fr.delcey.logino.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.delcey.logino.domain.home.GetHomesUseCase
import fr.delcey.logino.domain.home.model.HomeEntity
import fr.delcey.logino.domain.utils.HttpResult
import fr.delcey.logino.ui.R
import fr.delcey.logino.ui.mapper.HomeMapper
import fr.delcey.logino.ui.navigation.To
import fr.delcey.logino.ui.utils.EquatableCallbackWithParam
import fr.delcey.logino.ui.utils.Event
import fr.delcey.logino.ui.utils.NativeText
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getHomesUseCase: GetHomesUseCase,
    private val homeMapper: HomeMapper,
) : ViewModel() {

    private val refreshTriggerMutableSharedFlow = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    ).apply {
        tryEmit(Unit)
    }

    val uiStateLiveData: LiveData<MainUiState> = liveData {
        refreshTriggerMutableSharedFlow.collectLatest {
            emit(MainUiState.Loading)

            when (val homesResult = getHomesUseCase.invoke()) {
                is HttpResult.Failure ->
                    emit(
                        MainUiState.Error(
                            errorMessage = if (homesResult.isFromUserConnectivity) {
                                NativeText.Resource(R.string.error_connectivity)
                            } else {
                                NativeText.Resource(R.string.error_generic)
                            },
                        )
                    )

                is HttpResult.Success -> {
                    val items = homesResult.data.map { home ->
                        mapToMainItem(home)
                    }

                    emit(
                        MainUiState.Content(items = items)
                    )
                }
            }
        }
    }

    private val eventMutableLiveData = MutableLiveData<Event<MainEvent>>()
    val eventLiveData: LiveData<Event<MainEvent>> = eventMutableLiveData

    fun onPullToRefresh() {
        refreshTriggerMutableSharedFlow.tryEmit(Unit)
    }

    private fun mapToMainItem(home: HomeEntity): MainItemUiState = MainItemUiState(
        id = home.id,
        photoUrl = home.photoUrl,
        vendor = NativeText.Simple(home.vendor),
        price = homeMapper.getHomePrice(home.price),
        pricePerSquareMeter = homeMapper.getHomePricePerSquareMeter(home.price, home.area),
        propertyType = NativeText.Simple(home.propertyType),
        roomsAndSize = homeMapper.getRoomsAndSize(
            rooms = home.rooms,
            bedrooms = home.bedrooms,
            area = home.area,
        ),
        city = NativeText.Simple(home.city),
        onClick = EquatableCallbackWithParam { sharedElements ->
            val photoUrl = home.photoUrl
            eventMutableLiveData.value = Event(
                MainEvent.Navigate(
                    to = To.HomeDetail(
                        id = home.id,
                        uniqueSharedElementName = photoUrl,
                        sharedElements = sharedElements,
                    )
                )
            )
        }
    )
}
