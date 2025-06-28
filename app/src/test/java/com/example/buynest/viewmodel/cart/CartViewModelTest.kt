package com.example.buynest.viewmodel.cart

import android.util.Log
import com.apollographql.apollo3.api.ApolloResponse
import com.example.buynest.GetCartQuery
import com.example.buynest.RemoveItemFromCartMutation
import com.example.buynest.admin.CreateDraftOrderMutation
import com.example.buynest.model.entity.AddressModel
import com.example.buynest.model.entity.CartItem
import com.example.buynest.model.repository.cart.CartRepository
import com.example.buynest.model.repository.order.IOrderRepo
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    private val cartRepository = mockk<CartRepository>()
    private val orderRepo = mockk<IOrderRepo>()
    private lateinit var viewModel: CartViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        viewModel = CartViewModel(cartRepository, orderRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    @Test
    fun `getCart should update cartResponse`() = runTest {
        val cartId = "123"
        val mockResponse = mockk<ApolloResponse<GetCartQuery.Data>>()
        coEvery { cartRepository.getCart(cartId) } returns mockResponse

        viewModel.getCart(cartId)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(mockResponse, viewModel.cartResponse.value)
    }

    @Test
    fun `removeItemFromCart should update removeItemResponse`() = runTest {
        val cartId = "123"
        val lineId = "456"
        val mockResponse = mockk<ApolloResponse<RemoveItemFromCartMutation.Data>>()
        coEvery { cartRepository.removeItem(cartId, lineId) } returns mockResponse

        viewModel.removeItemFromCart(cartId, lineId)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(mockResponse, viewModel.removeItemResponse.value)
    }

    @Test
    fun `getOrderModelFromCart should trigger draft order creation`() = runTest {
        val email = "test@example.com"
        val address = AddressModel("1", "John", "Doe", "Street 1", "", "City", "Country", "12345")
        val items = listOf(
            CartItem(
                id = 1,
                lineId = "line_1",
                name = "Test Product",
                price = 100,
                color = "Black",
                size = 42,
                imageUrl = "http://example.com/image.png",
                quantity = 2,
                variantId = "variant_1",
                maxQuantity = 5,
                currencySymbol = "$"
            )
        )

        val mockResponse = mockk<ApolloResponse<CreateDraftOrderMutation.Data>>(relaxed = true)
        coEvery { orderRepo.draftOrder(any()) } returns mockResponse

        viewModel.getOrderModelFromCart(email, address, items, isPaid = true, discount = 10.0)
        advanceUntilIdle()

        assertEquals(mockResponse, viewModel.orderResponse.value)

        coVerify {
            orderRepo.draftOrder(withArg {
                assertEquals(email, it.email)
                assertEquals(10.0, it.discount, 0.0)
                assertEquals(1, it.orderItems.size)
            })
        }
    }
}
