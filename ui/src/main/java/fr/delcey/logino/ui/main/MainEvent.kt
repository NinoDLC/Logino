package fr.delcey.logino.ui.main

import fr.delcey.logino.ui.navigation.To

sealed class MainEvent {
    data class Navigate(val to: To) : MainEvent()
}
