package com.example.buynest.repository.productDetails

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.exception.ApolloException
import com.example.buynest.ProductDetailsByIDQuery
import com.example.buynest.model.repository.productDetails.ProductDetailsRepositoryImpl
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull


@OptIn(ExperimentalCoroutinesApi::class)
class ProductDetailsRepositoryImplTest {

    private lateinit var apolloClient: ApolloClient
    private lateinit var repository: ProductDetailsRepositoryImpl

    @Before
    fun setup() {
        apolloClient = mockk(relaxed = true)
        repository = ProductDetailsRepositoryImpl(apolloClient)
    }

    @Test
    fun `getProductDetails should return data when successful`() = runTest {
        val productId = "gid://shopify/Product/123"
        val query = ProductDetailsByIDQuery(productId)

        val mockData = mockk<ProductDetailsByIDQuery.Data>(relaxed = true)
        val uuid = com.benasher44.uuid.uuid4()

        val response = ApolloResponse.Builder(
            operation = query,
            requestUuid = uuid,
            data = mockData
        ).build()

        val apolloCall = mockk<ApolloCall<ProductDetailsByIDQuery.Data>>()
        coEvery { apolloCall.execute() } returns response
        every { apolloClient.query(query) } returns apolloCall

        val result = repository.getProductDetails(productId).first()

        assertNotNull(result)
        assertEquals(mockData, result)
    }

    @Test
    fun `getProductDetails should emit null on error`() = runTest {
        val productId = "invalid"

        val query = ProductDetailsByIDQuery(productId)
        val apolloCall = mockk<ApolloCall<ProductDetailsByIDQuery.Data>>()

        coEvery { apolloCall.execute() } throws ApolloException("Simulated error")
        every { apolloClient.query(query) } returns apolloCall

        val result = repository.getProductDetails(productId).first()

        assertNull(result)
    }
}
