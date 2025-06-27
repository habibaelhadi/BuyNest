package com.example.buynest.viewmodel.shared

import com.example.buynest.BrandsAndProductsQuery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import io.mockk.mockk

@OptIn(ExperimentalCoroutinesApi::class)
class SharedViewModelTest {

    private lateinit var viewModel: SharedViewModel

    @Before
    fun setup() {
        viewModel = SharedViewModel()
    }

    @Test
    fun `initial category state is empty`() = runTest {
        val initialValue = viewModel.category.first()
        assertEquals(emptyList<BrandsAndProductsQuery.Node3>(), initialValue)
    }

    @Test
    fun `setCategories updates category with non-empty list`() = runTest {
        val mockList = listOf(mockk<BrandsAndProductsQuery.Node3>(), mockk())

        viewModel.setCategories(mockList)

        val updatedValue = viewModel.category.first()
        assertEquals(mockList, updatedValue)
    }
}
