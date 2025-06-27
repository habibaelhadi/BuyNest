// ProductDetailsViewModelTest.kt
package com.example.buynest.viewmodel.productInfo

import com.apollographql.apollo3.api.ApolloResponse
import com.example.buynest.CreateCartMutation
import com.example.buynest.ProductDetailsByIDQuery
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.repository.productDetails.ProductDetailsRepository
import com.example.buynest.utils.AppConstants.KEY_CART_ID
import com.example.buynest.utils.SecureSharedPrefHelper
import com.example.buynest.viewmodel.cart.CartUseCase
import io.mockk.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductDetailsViewModelTest {

    private lateinit var viewModel: ProductDetailsViewModel
    private val repository = mockk<ProductDetailsRepository>(relaxed = true)
    private val cartUseCase = mockk<CartUseCase>(relaxed = true)

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ProductDetailsViewModel(repository, cartUseCase)

        mockkObject(SecureSharedPrefHelper)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `getProductDetails emits Success when data is returned`() = runTest {
        val dummyData = mockk<ProductDetailsByIDQuery.Data>(relaxed = true)

        coEvery { repository.getProductDetails("123") } returns flow {
            emit(dummyData)
        }

        viewModel.getProductDetails("123")

        advanceUntilIdle() // Wait for coroutines to finish

        // Assert the latest value
        assert(viewModel.productDetails.value is UiResponseState.Success<*>)
        assertEquals(dummyData, (viewModel.productDetails.value as UiResponseState.Success<*>).data)
    }



}
