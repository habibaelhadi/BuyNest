package com.example.buynest.repository.cart

import com.apollographql.apollo3.api.ApolloResponse
import com.example.buynest.CreateCartMutation
import com.example.buynest.GetCartQuery
import com.example.buynest.LinkCartToCustomerMutation
import com.example.buynest.RemoveItemFromCartMutation
import com.example.buynest.model.repository.cart.CartRepository
import com.example.buynest.model.repository.cart.CartRepositoryImpl
import com.example.buynest.model.repository.cart.datasource.CartDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CartRepositoryImplTest {

    private lateinit var dataSource: CartDataSource
    private lateinit var repository: CartRepository

    private val mockCreateCartResponse = mockk<ApolloResponse<CreateCartMutation.Data>>()
    private val mockLinkResponse = mockk<ApolloResponse<LinkCartToCustomerMutation.Data>>()
    private val mockGetCartResponse = mockk<ApolloResponse<GetCartQuery.Data>>()
    private val mockRemoveResponse = mockk<ApolloResponse<RemoveItemFromCartMutation.Data>>()
    private val mockAddItemResponse = mockk<ApolloResponse<*>>() // wildcard

    @Before
    fun setup() {
        dataSource = mockk(relaxed = true)
        repository = CartRepositoryImpl(dataSource)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `test createCart calls dataSource and returns response`() = runTest {
        coEvery { dataSource.createCart() } returns mockCreateCartResponse

        val result = repository.createCart()

        assertEquals(mockCreateCartResponse, result)
        coVerify { dataSource.createCart() }
    }

    @Test
    fun `test linkCart calls dataSource and returns response`() = runTest {
        val cartId = "123"
        val token = "abc"
        coEvery { dataSource.linkToCustomer(cartId, token) } returns mockLinkResponse

        val result = repository.linkCart(cartId, token)

        assertEquals(mockLinkResponse, result)
        coVerify { dataSource.linkToCustomer(cartId, token) }
    }

    @Test
    fun `test getCart calls dataSource and returns response`() = runTest {
        val cartId = "123"
        coEvery { dataSource.getCart(cartId) } returns mockGetCartResponse

        val result = repository.getCart(cartId)

        assertEquals(mockGetCartResponse, result)
        coVerify { dataSource.getCart(cartId) }
    }

    @Test
    fun `test addItemToCart calls dataSource and returns response`() = runTest {
        val cartId = "123"
        val variantId = "v456"
        val quantity = 2
        coEvery { dataSource.addItemToCart(cartId, variantId, quantity) } returns mockAddItemResponse

        val result = repository.addItemToCart(cartId, variantId, quantity)

        assertEquals(mockAddItemResponse, result)
        coVerify { dataSource.addItemToCart(cartId, variantId, quantity) }
    }

    @Test
    fun `test removeItem calls dataSource and returns response`() = runTest {
        val cartId = "123"
        val lineId = "line789"
        coEvery { dataSource.removeItem(cartId, lineId) } returns mockRemoveResponse

        val result = repository.removeItem(cartId, lineId)

        assertEquals(mockRemoveResponse, result)
        coVerify { dataSource.removeItem(cartId, lineId) }
    }
}
