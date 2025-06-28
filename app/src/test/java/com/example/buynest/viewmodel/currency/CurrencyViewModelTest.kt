package com.example.buynest.viewmodel.currency

import android.content.Context
import com.example.buynest.model.data.local.CurrencyEntity
import com.example.buynest.model.repository.currency.ICurrencyRepository
import com.example.buynest.utils.CurrencyHelper
import com.example.buynest.utils.CurrencyHelper.getCurrencyName
import com.example.buynest.utils.CurrencyHelper.getCurrencySymbol
import com.example.buynest.utils.SharedPrefHelper
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class CurrencyViewModelTest {

    private lateinit var repository: ICurrencyRepository
    private lateinit var viewModel: CurrencyViewModel
    private lateinit var context: Context

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        context = mockk(relaxed = true)

        mockkObject(SharedPrefHelper)
        mockkObject(CurrencyHelper)

        viewModel = CurrencyViewModel(repository, context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `loadCurrency should update rate and symbol`() = runTest {
        // Given
        val base = "EGP"
        val sharedPrefCountryCode = "US"
        val currencyCode = "USD"
        val currencySymbol = "$"
        val currencyRate = 0.03

        every { SharedPrefHelper.getCurrency(context) } returns sharedPrefCountryCode
        every { getCurrencyName(sharedPrefCountryCode) } returns currencyCode
        every { getCurrencySymbol(currencyCode) } returns currencySymbol

        val fakeCurrencyEntity = CurrencyEntity(
            base = base,
            rates = mapOf(currencyCode to currencyRate),
            lastUpdated = System.currentTimeMillis()
        )
        coEvery { repository.getCurrencyRates(base, context) } returns fakeCurrencyEntity

        // When
        viewModel.loadCurrency(base)
        advanceUntilIdle()

        // Then
        assertEquals(currencyRate, viewModel.rate.value)
        assertEquals(currencySymbol, viewModel.currencySymbol.value)
    }
}
