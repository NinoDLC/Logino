package fr.delcey.logino.ui.home_detail

import fr.delcey.logino.ui.utils.NativeText

sealed class HomeDetailUiState {
    data object Loading : HomeDetailUiState()
    data class Error(val errorMessage: NativeText) : HomeDetailUiState()
    data class Content(
        val photoUrl: String?,
        val vendor: NativeText,
        val price: NativeText,
        val pricePerSquareMeter: NativeText,
        val propertyType: NativeText,
        val roomsAndSize: NativeText,
        val city: NativeText,
    ) : HomeDetailUiState()
}
