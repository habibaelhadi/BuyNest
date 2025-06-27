package com.example.buynest.repository.currency

import android.content.Context
import com.example.buynest.model.data.local.CurrencyEntity
import com.example.buynest.repository.currency.datasource.ICurrencyDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CurrencyRepositoryImplTest {
    private lateinit var mockDataSource: ICurrencyDataSource
    private lateinit var repository: CurrencyRepositoryImpl
    private lateinit var mockContext: Context

    @Before
    fun setup() {
        mockDataSource = mockk()
        repository = CurrencyRepositoryImpl(mockDataSource)
        mockContext = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getExchangeRate should return rate from data source`() = runTest {
        val expectedRate = 30.5
        coEvery { mockDataSource.getFetchTargetRate(mockContext) } returns expectedRate

        val result = repository.getExchangeRate(mockContext)

        assertEquals(expectedRate, result, 0.0)
        coVerify { mockDataSource.getFetchTargetRate(mockContext) }
    }

    @Test
    fun `getCurrencyRates should fetch and store when no local data`() = runTest {
        val fetched = CurrencyEntity("EGP", mapOf("USD" to 30.0), System.currentTimeMillis())
        coEvery { mockDataSource.getLocalRates("EGP") } returns null
        coEvery { mockDataSource.fetchAndStoreRates(mockContext) } returns fetched

        val result = repository.getCurrencyRates("EGP", mockContext)

        assertEquals(fetched, result)
        coVerify { mockDataSource.fetchAndStoreRates(mockContext) }
    }

    @Test
    fun `getCurrencyRates should fetch and store when local data is outdated`() = runTest {
        val oldData = CurrencyEntity("EGP", mapOf("USD" to 28.0), System.currentTimeMillis() - (25 * 60 * 60 * 1000)) // 25 hours ago
        val updatedData = CurrencyEntity("EGP", mapOf("USD" to 30.0), System.currentTimeMillis())

        coEvery { mockDataSource.getLocalRates("EGP") } returns oldData
        coEvery { mockDataSource.fetchAndStoreRates(mockContext) } returns updatedData

        val result = repository.getCurrencyRates("EGP", mockContext)

        assertEquals(updatedData, result)
        coVerify { mockDataSource.fetchAndStoreRates(mockContext) }
    }

    @Test
    fun `getCurrencyRates should return local data when not outdated`() = runTest {
        val freshData = CurrencyEntity("EGP", mapOf("USD" to 29.0), System.currentTimeMillis())
        coEvery { mockDataSource.getLocalRates("EGP") } returns freshData

        val result = repository.getCurrencyRates("EGP", mockContext)

        assertEquals(freshData, result)
        coVerify(exactly = 0) { mockDataSource.fetchAndStoreRates(mockContext) }
    }
}
