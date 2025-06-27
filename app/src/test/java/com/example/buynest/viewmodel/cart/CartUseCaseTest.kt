package com.example.buynest.viewmodel.cart

import com.apollographql.apollo3.api.ApolloResponse
import com.example.buynest.CreateCartMutation
import com.example.buynest.LinkCartToCustomerMutation
import com.example.buynest.repository.cart.CartRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class CartUseCaseTest {

    private lateinit var repository: CartRepository
    private lateinit var useCase: CartUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = CartUseCase(repository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `createCart should return ApolloResponse`() = runTest {
        val expectedResponse = mockk<ApolloResponse<CreateCartMutation.Data>>()
        coEvery { repository.createCart() } returns expectedResponse

        val result = useCase.createCart()

        assertEquals(expectedResponse, result)
        coVerify { repository.createCart() }
    }

    @Test
    fun `linkCartToCustomer should call repository and return response`() = runTest {
        val cartId = "cart123"
        val token = "token123"
        val expectedResponse = mockk<ApolloResponse<LinkCartToCustomerMutation.Data>>()
        coEvery { repository.linkCart(cartId, token) } returns expectedResponse

        val result = useCase.linkCartToCustomer(cartId, token)

        assertEquals(expectedResponse, result)
        coVerify { repository.linkCart(cartId, token) }
    }

    @Test
    fun `addOrUpdateCartItem should call repository and return response`() = runTest {
        val cartId = "cart456"
        val variantId = "variant789"
        val quantity = 3
        val expectedResponse = mockk<ApolloResponse<*>>()
        coEvery { repository.addItemToCart(cartId, variantId, quantity) } returns expectedResponse

        val result = useCase.addOrUpdateCartItem(cartId, variantId, quantity)

        assertEquals(expectedResponse, result)
        coVerify { repository.addItemToCart(cartId, variantId, quantity) }
    }
}
