// ProductDetailsViewModelTest.kt
package com.example.buynest.viewmodel.productInfo

import com.apollographql.apollo3.api.ApolloResponse
import com.example.buynest.CreateCartMutation
import com.example.buynest.ProductDetailsByIDQuery
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.model.repository.productDetails.ProductDetailsRepository
import com.example.buynest.utils.AppConstants.KEY_CART_ID
import com.example.buynest.utils.SecureSharedPrefHelper
import com.example.buynest.viewmodel.cart.CartUseCase
import io.mockk.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
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

        advanceUntilIdle()

        assert(viewModel.productDetails.value is UiResponseState.Success<*>)
        assertEquals(dummyData, (viewModel.productDetails.value as UiResponseState.Success<*>).data)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `addToCart should create cart and add item when cartId is null`() = runTest {
        val variantId = "variant123"
        val quantity = 2
        val expectedCartId = "generatedCartId"

        every { SecureSharedPrefHelper.getString(KEY_CART_ID) } returns null

        val mockCart = mockk<CreateCartMutation.Cart>(relaxed = true)
        every { mockCart.id } returns expectedCartId

        val mockCartCreate = mockk<CreateCartMutation.CartCreate>(relaxed = true)
        every { mockCartCreate.cart } returns mockCart

        val mockData = mockk<CreateCartMutation.Data>(relaxed = true)
        every { mockData.cartCreate } returns mockCartCreate

        val operation = mockk<CreateCartMutation>(relaxed = true)
        val uuid = com.benasher44.uuid.uuid4()

        val mockResponse = ApolloResponse.Builder(
            operation = operation,
            requestUuid = uuid,
            data = mockData
        ).build()

        coEvery { cartUseCase.createCart() } returns mockResponse

        val mockAddResponse = mockk<ApolloResponse<*>>(relaxed = true)
        coEvery { cartUseCase.addOrUpdateCartItem(expectedCartId, variantId, quantity) } returns mockAddResponse
        every { mockAddResponse.hasErrors() } returns false

        every { SecureSharedPrefHelper.putString(KEY_CART_ID, expectedCartId) } returns Unit

        viewModel.addToCart(variantId, quantity)

        coVerify { cartUseCase.createCart() }
        coVerify { cartUseCase.addOrUpdateCartItem(expectedCartId, variantId, quantity) }
        verify { SecureSharedPrefHelper.putString(KEY_CART_ID, expectedCartId) }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `addToCart should use existing cartId if available`() = runTest {
        val variantId = "variant456"
        val quantity = 1
        val existingCartId = "existingCartId"

        every { SecureSharedPrefHelper.getString(KEY_CART_ID) } returns existingCartId

        val mockAddResponse = mockk<ApolloResponse<*>>(relaxed = true)
        coEvery { cartUseCase.addOrUpdateCartItem(existingCartId, variantId, quantity) } returns mockAddResponse
        every { mockAddResponse.hasErrors() } returns false

        viewModel.addToCart(variantId, quantity)

        coVerify(exactly = 0) { cartUseCase.createCart() }
        coVerify { cartUseCase.addOrUpdateCartItem(existingCartId, variantId, quantity) }
        verify(exactly = 0) { SecureSharedPrefHelper.putString(any(), any()) }
    }

}
