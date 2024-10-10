package fr.delcey.logino.ui

import org.junit.Assert.*
import org.junit.Test

class UiModuleTest {
    @Test
    fun `verify provideCurrencyNumberFormat`() {
        // When
        val currencyNumberFormat = UiModule().provideCurrencyNumberFormat()

        // Then
        assertEquals(
            0,
            currencyNumberFormat.maximumFractionDigits
        )
    }
}