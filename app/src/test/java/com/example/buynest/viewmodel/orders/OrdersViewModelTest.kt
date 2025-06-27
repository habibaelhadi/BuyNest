package com.example.buynest.viewmodel.orders

import com.example.buynest.admin.GetOrdersByEmailQuery
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.repository.order.IOrderRepo
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrdersViewModelTest {

    private lateinit var repository: IOrderRepo
    private lateinit var viewModel: OrdersViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repository = mockk()
        viewModel = OrdersViewModel(repository)
    }

    @Test
    fun `getAllOrders should emit Success when data is received`() = runTest {
        val email = "test@example.com"

        val mockData = mockk<GetOrdersByEmailQuery.Data>()
        val mockEdge = mockk<GetOrdersByEmailQuery.Edge>()
        val mockNode = mockk<GetOrdersByEmailQuery.Node>()

        every { mockEdge.node } returns mockNode
        every { mockData.orders.edges } returns listOf(mockEdge)

        coEvery { repository.getAllOrders(email) } returns flowOf(mockData)

        viewModel.getAllOrders(email)
        advanceUntilIdle()

        val state = viewModel.orders.value
        assertTrue("Expected Success but got $state", state is UiResponseState.Success<*>)

        val resultData = (state as UiResponseState.Success<*>).data
        assertEquals(mockData, resultData)
    }

    @Test
    fun `setSelectedOrder should update selectedOrder value`() {
        val mockOrder = mockk<GetOrdersByEmailQuery.Node>(relaxed = true)
        viewModel.setSelectedOrder(mockOrder)
        assertEquals(mockOrder, viewModel.selectedOrder.value)
    }

    @Test
    fun `extractImageUrlsFromNote should return correct URLs`() {
        val note = """
            Image: https://example.com/image1.jpg
            Image: https://example.com/image2.jpg
        """.trimIndent()

        val result = viewModel.extractImageUrlsFromNote(note)
        assertEquals(
            listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg"),
            result
        )
    }


    @Test
    fun `extractPriceDetailsFromNote should return correct triple`() {
        val note = """
            TotalBefore: 200
            Discount: 50
            TotalAfter: 150
        """.trimIndent()

        val result = viewModel.extractPriceDetailsFromNote(note)
        assertEquals(Triple(200, 50, 150), result)
    }

    @Test
    fun `extractPriceDetailsFromNote should return zeros for invalid or missing data`() {
        val note = "Random text without prices"
        val result = viewModel.extractPriceDetailsFromNote(note)
        assertEquals(Triple(0, 0, 0), result)
    }
}
