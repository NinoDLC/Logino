package fr.delcey.logino.ui.main

import android.view.View
import fr.delcey.logino.ui.utils.EquatableCallbackWithParam
import fr.delcey.logino.ui.utils.NativeText

data class MainItemUiState(
    val id: Long,
    val photoUrl: String?,
    val vendor: NativeText,
    val price: NativeText,
    val pricePerSquareMeter: NativeText,
    val propertyType: NativeText,
    val roomsAndSize: NativeText,
    val city: NativeText,
    val onClick: EquatableCallbackWithParam<List<View>>,
)
