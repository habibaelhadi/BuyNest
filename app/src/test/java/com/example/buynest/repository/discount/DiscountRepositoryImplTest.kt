package com.example.buynest.repository.discount

import com.example.buynest.model.entity.OfferModel
import com.example.buynest.model.repository.discount.DiscountRepositoryImpl
import com.example.buynest.model.repository.discount.datasource.ShopifyDiscountDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
class DiscountRepositoryImplTest {

    private lateinit var dataSource: ShopifyDiscountDataSource
    private lateinit var repository: DiscountRepositoryImpl

    @Before
    fun setUp() {
        dataSource = mockk()
        repository = DiscountRepositoryImpl(dataSource)
    }

    @Test
    fun `getAllDiscounts should return list of offers`() = runTest {
        val expectedOffers = listOf(
            OfferModel("SAVE10", "10% off", "Apply", 10.0, 101),
            OfferModel("SAVE20", "20% off", "Use Now", 20.0, 102)
        )
        coEvery { dataSource.fetchDiscounts() } returns expectedOffers

        val result = repository.getAllDiscounts()

        assertEquals(expectedOffers, result)
        coVerify { dataSource.fetchDiscounts() }
    }

    @Test
    fun `isCouponValid should return true if valid`() = runTest {
        coEvery { dataSource.isCouponValid("SAVE10") } returns true

        val result = repository.isCouponValid("SAVE10")

        assertTrue(result)
        coVerify { dataSource.isCouponValid("SAVE10") }
    }

    @Test
    fun `getDiscountAmount should return correct value`() = runTest {
        coEvery { dataSource.getDiscountAmount("SAVE10") } returns 10.0

        val result = repository.getDiscountAmount("SAVE10")

        assertEquals(10.0, result, 0.0)
        coVerify { dataSource.getDiscountAmount("SAVE10") }
    }
}
