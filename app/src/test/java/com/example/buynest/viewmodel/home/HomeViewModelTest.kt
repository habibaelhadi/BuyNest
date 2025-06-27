package com.example.buynest.viewmodel.home

import android.content.Context
import com.example.buynest.BrandsAndProductsQuery
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.repository.home.IHomeRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertTrue
import kotlin.test.assertEquals


class HomeViewModelTest {
    private lateinit var repository: IHomeRepository
    private lateinit var viewModel: HomeViewModel
    @OptIn(ExperimentalCoroutinesApi::class)
    private val dispatcher = UnconfinedTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repository = mockk(relaxed = true)
        viewModel = HomeViewModel(repository)
    }

    @Test
    fun `getBrands should emit Success state when data is valid`() = runTest {
        val mockContext = mockk<Context>(relaxed = true)
        val mockData = mockk<BrandsAndProductsQuery.Data>()
        val mockProducts = mockk<BrandsAndProductsQuery.Products>()
        val mockCollections = mockk<BrandsAndProductsQuery.Collections>()
        val mockProductNode = mockk<BrandsAndProductsQuery.Node>()
        val mockProductEdge = mockk<BrandsAndProductsQuery.Edge>()
        val mockBrandNode = mockk<BrandsAndProductsQuery.Node3>()

        every { mockProductEdge.node } returns mockProductNode
        every { mockProducts.edges } returns listOf(mockProductEdge)
        every { mockCollections.nodes } returns listOf(mockBrandNode)
        every { mockData.products } returns mockProducts
        every { mockData.collections } returns mockCollections

        coEvery { repository.getBrands() } returns flowOf(mockData)

        viewModel.getBrands(mockContext)
        advanceUntilIdle()

        val state = viewModel.brand.value
        assertTrue("Expected Success but got $state", state is UiResponseState.Success<*>)

        val (brands, products) = (state as UiResponseState.Success<Pair<*, *>>).data
        assertEquals(listOf(mockBrandNode), brands)
        assertEquals(listOf(mockProductNode), products)
    }

    @Test
    fun `getBrands should emit Error state when data is null`() = runTest {
        val mockContext = mockk<Context>(relaxed = true)
        coEvery { repository.getBrands() } returns flowOf(null)

        viewModel.getBrands(mockContext)
        advanceUntilIdle()

        val state = viewModel.brand.value
        assertTrue("Expected Error but got $state", state is UiResponseState.Error)
    }
}