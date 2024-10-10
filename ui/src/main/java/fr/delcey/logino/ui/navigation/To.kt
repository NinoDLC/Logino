package fr.delcey.logino.ui.navigation

import android.view.View

sealed class To {
    data class HomeDetail(
        val id: Long,
        val uniqueSharedElementName: String?,
        val sharedElements: List<View>,
    ) : To()
}
