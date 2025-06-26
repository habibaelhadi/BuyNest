package com.example.buynest.viewmodel.discount

import org.junit.Assert.*
import com.example.buynest.model.entity.OfferModel
import com.example.buynest.repository.discount.DiscountRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DiscountViewModelTest {

    private lateinit var repository: DiscountRepository
    private lateinit var viewModel: DiscountViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = DiscountViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadDiscounts should update offers`() = runTest {
        val mockOffers = listOf(
            OfferModel(
                title = "Summer Sale",
                subtitle = "Enjoy 15% off!",
                buttonText = "Shop Now",
                percentage = 15.0,
                imageRes = 123
            ),
            OfferModel(
                title = "Winter Sale",
                subtitle = "Hot deals inside",
                buttonText = "Grab it",
                percentage = 20.0,
                imageRes = 456
            )
        )

        coEvery { repository.getAllDiscounts() } returns mockOffers

        viewModel.loadDiscounts()
        advanceUntilIdle()

        assertEquals(mockOffers, viewModel.offers.value)
    }

    @Test
    fun `isCouponValid should return true for valid coupon`() = runTest {
        val coupon = "SAVE20"
        coEvery { repository.isCouponValid(coupon) } returns true

        val result = viewModel.isCouponValid(coupon)
        assertTrue(result)
    }

    @Test
    fun `applyCoupon should return correct discount amount`() = runTest {
        val coupon = "SAVE10"
        val expectedDiscount = 0.10

        coEvery { repository.getDiscountAmount(coupon) } returns expectedDiscount

        val result = viewModel.applyCoupon(coupon)
        assertEquals(expectedDiscount, result)
    }
}
