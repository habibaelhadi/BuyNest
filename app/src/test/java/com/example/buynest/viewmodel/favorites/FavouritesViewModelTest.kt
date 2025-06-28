package com.example.buynest.viewmodel.favorites

import com.apollographql.apollo3.api.ApolloResponse
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.model.repository.favorite.FavoriteRepo
import com.example.buynest.utils.AppConstants.KEY_CART_ID
import com.example.buynest.utils.SecureSharedPrefHelper
import com.example.buynest.viewmodel.cart.CartUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals


class FavouritesViewModelTest {

    private lateinit var viewModel: FavouritesViewModel
    private lateinit var repo: FavoriteRepo
    private lateinit var cartUseCase: CartUseCase

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        repo = mockk(relaxed = true)
        cartUseCase = mockk(relaxed = true)
        mockkObject(SecureSharedPrefHelper)

        viewModel = FavouritesViewModel(repo, cartUseCase)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `addToFavorite should update favorite list on success`() = runTest {
        val productId = "123"
        coEvery { repo.addToFavorite(productId) } returns Result.success(Unit)

        viewModel.addToFavorite(productId)
        advanceUntilIdle()
        assertEquals(listOf(productId), viewModel.favorite.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `removeFromFavorite should remove item from list on success`() = runTest {
        val productId = "123"
        viewModel.favorite.value = listOf("123", "456")
        coEvery { repo.removeFromFavorite(productId) } returns Result.success(Unit)

        viewModel.removeFromFavorite(productId)
        advanceUntilIdle()
        assertEquals(listOf("456"), viewModel.favorite.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getAllFavorites should update favorite list and call getProductsByIds`() = runTest {
        val productIds = listOf("id1", "id2")

        coEvery { repo.getAllFavorites() } returns Result.success(productIds)

        coEvery { repo.getProductsByIds(productIds) } returns flowOf(null)

        viewModel.getAllFavorites()
        advanceUntilIdle()

        assertEquals(productIds, viewModel.favorite.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getProductsByIds should emit Success when data is not null`() = runTest {
        val productIds = listOf("id1", "id2")
        val mockData = mockk<com.example.buynest.ProductsDetailsByIDsQuery.Data>(relaxed = true)

        coEvery { repo.getProductsByIds(productIds) } returns flowOf(mockData)

        viewModel.getProductsByIds(productIds)
        advanceUntilIdle()

        val result = viewModel.productDetails.value
        assertEquals(true, result is UiResponseState.Success<*>)
        assertEquals(mockData, (result as UiResponseState.Success<*>).data)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `addToCart should call cartUseCase when cartId is available`() = runTest {
        val variantId = "variant123"
        val quantity = 2
        val cartId = "mock_cart_id"

        every { SecureSharedPrefHelper.getString(KEY_CART_ID) } returns cartId

        val mockResponse = mockk<ApolloResponse<*>>()
        every { mockResponse.hasErrors() } returns false

        coEvery {
            cartUseCase.addOrUpdateCartItem(cartId, variantId, quantity)
        } returns mockResponse

        viewModel.addToCart(variantId, quantity)

        coVerify {
            cartUseCase.addOrUpdateCartItem(cartId, variantId, quantity)
        }
    }

}