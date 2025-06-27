package com.example.buynest.viewmodel.brandproducts

import com.example.buynest.ProductsByCollectionIDQuery
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.repository.home.IHomeRepository
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import kotlin.test.Test


class BrandDetailsViewModelTest {
    private lateinit var repo: IHomeRepository
    private lateinit var viewModel: BrandDetailsViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repo = mockk(relaxed = true)
        viewModel = BrandDetailsViewModel(repo)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getBrandProducts should update brandProducts state`() = runTest {
        val fakeId = "123"
        val fakeData = mockk<ProductsByCollectionIDQuery.Data>(relaxed = true)

        coEvery { repo.getBrandProducts(fakeId) } returns flowOf(fakeData)

        viewModel.getBrandProducts(fakeId)

        advanceUntilIdle()

        val finalState = viewModel.brandProducts.value
        println("Final state: $finalState")
        assertTrue("Expected Success, but got: $finalState", finalState is UiResponseState.Success<*>)
    }


}