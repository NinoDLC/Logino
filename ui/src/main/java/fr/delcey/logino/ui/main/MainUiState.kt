package fr.delcey.logino.ui.main

import fr.delcey.logino.ui.utils.NativeText

sealed class MainUiState {
    data object Loading : MainUiState()
    data class Error(val errorMessage: NativeText) : MainUiState()
    data class Content(val items: List<MainItemUiState>) : MainUiState()
}