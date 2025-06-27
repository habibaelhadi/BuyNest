package com.example.buynest.viewmodel.categoryViewModel

import com.example.buynest.ProductsByHandleQuery
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.repository.category.ICategoryRepo
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.Assert.assertTrue


class CategoryViewModelTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repo: ICategoryRepo
    private lateinit var viewModel: CategoryViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repo = mockk(relaxed = true)
        viewModel = CategoryViewModel(repo)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getCategoryProducts should emit Success state and update selected category`() = runTest {
        val fakeCategory = "Shoes"
        val fakeData = mockk<ProductsByHandleQuery.Data>(relaxed = true)

        coEvery { repo.getProductByCategoryName(fakeCategory) } returns flowOf(fakeData)

        viewModel.getCategoryProducts(fakeCategory)
        advanceUntilIdle()

        val state = viewModel.categoryProducts.value
        val selectedCategory = viewModel.selectedCategory.value

        assertEquals(fakeCategory, selectedCategory)
        assertEquals(fakeData, (state as UiResponseState.Success<*>).data)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getCategoryProducts should emit Error state on null result`() = runTest {
        val fakeCategory = "Accessories"

        coEvery { repo.getProductByCategoryName(fakeCategory) } returns flowOf(null)

        viewModel.getCategoryProducts(fakeCategory)
        advanceUntilIdle()

        val state = viewModel.categoryProducts.value
        assertTrue("Expected Error but got $state", state is UiResponseState.Error)
    }

    @Test
    fun `setSelectedCategory should update selectedCategory`() {
        viewModel.setSelectedCategory("Bags")
        assertEquals("Bags", viewModel.selectedCategory.value)
    }

}