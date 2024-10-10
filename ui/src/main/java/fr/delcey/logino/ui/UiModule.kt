package fr.delcey.logino.ui

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.text.NumberFormat
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UiModule {

    @Singleton
    @Provides
    fun provideCurrencyNumberFormat(): NumberFormat = NumberFormat.getCurrencyInstance().apply {
        maximumFractionDigits = 0
    }
}