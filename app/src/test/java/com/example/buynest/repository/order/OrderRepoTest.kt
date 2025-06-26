package com.example.buynest.repository.order

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.exception.ApolloException
import com.example.buynest.admin.CompleteDraftOrderMutation
import com.example.buynest.admin.CreateDraftOrderMutation
import com.example.buynest.admin.GetOrdersByEmailQuery
import com.example.buynest.model.entity.OrderModel
import com.example.buynest.model.mapper.toDraftOrderInput
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class OrderRepoTest {
    private lateinit var apolloClient: ApolloClient
    private lateinit var orderRepo: OrderRepo

    @Before
    fun setUp() {
        apolloClient = mockk(relaxed = true)
        orderRepo = OrderRepo(apolloClient)
    }

    @Test
    fun `draftOrder should return response with data`() = runTest {
        val order = mockk<OrderModel>(relaxed = true)
        val input = order.toDraftOrderInput()
        val mutation = CreateDraftOrderMutation(input)

        val data = mockk<CreateDraftOrderMutation.Data>(relaxed = true)
        val uuid = com.benasher44.uuid.uuid4()

        val response = ApolloResponse.Builder(
            operation = mutation,
            requestUuid = uuid,
            data = data
        ).build()

        coEvery { apolloClient.mutation(mutation).execute() } returns response

        val result = orderRepo.draftOrder(order)

        assertNotNull(result)
        assertEquals(data, result.data)
    }

    @Test
    fun `completeDraftOrder should return response with data`() = runTest {
        val draftOrderID = "gid://shopify/DraftOrder/123456789"
        val mutation = CompleteDraftOrderMutation(draftOrderID)

        val data = mockk<CompleteDraftOrderMutation.Data>(relaxed = true)
        val uuid = com.benasher44.uuid.uuid4()

        val response = ApolloResponse.Builder(
            operation = mutation,
            requestUuid = uuid,
            data = data
        ).build()

        coEvery { apolloClient.mutation(mutation).execute() } returns response

        val result = orderRepo.completeDraftOrder(draftOrderID)

        assertNotNull(result)
        assertEquals(data, result.data)
    }

    @Test
    fun `getAllOrders should emit data from response`() = runTest {
        val email = "test@example.com"
        val query = GetOrdersByEmailQuery(email)
        val data = mockk<GetOrdersByEmailQuery.Data>(relaxed = true)
        val uuid = com.benasher44.uuid.uuid4()

        val response = ApolloResponse.Builder(
            operation = query,
            requestUuid = uuid,
            data = data
        ).build()

        coEvery { apolloClient.query(query).execute() } returns response

        val result = orderRepo.getAllOrders(email).first()

        assertNotNull(result)
        assertEquals(data, result)
    }

    @Test
    fun `getAllOrders should not crash on error and skip emit`() = runTest {
        val email = "fail@example.com"
        val query = GetOrdersByEmailQuery(email)

        coEvery { apolloClient.query(query).execute() } throws ApolloException("Simulated failure")

        val emissions = mutableListOf<GetOrdersByEmailQuery.Data>()
        orderRepo.getAllOrders(email).catch {
            // Optionally assert log or fallback
        }.collect {
            emissions.add(it)
        }

        assertTrue(emissions.isEmpty())
    }
}