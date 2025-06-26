package com.example.buynest.repository.home

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.exception.ApolloException
import kotlinx.coroutines.flow.flowOf
import com.example.buynest.BrandsAndProductsQuery
import com.example.buynest.ProductsByCollectionIDQuery
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.util.UUID
import kotlin.test.assertNotNull

class HomeRepositoryTest {

    private lateinit var apolloClient: ApolloClient
    private lateinit var repository: HomeRepository

    @Before
    fun setUp() {
        apolloClient = mockk(relaxed = true)
        repository = HomeRepository(apolloClient)
    }

    @Test
    fun `getBrands should return data when successful`() = runTest {
        // Create a fake data object (you can mock this if it's simple)
        val mockData = mockk<BrandsAndProductsQuery.Data>(relaxed = true)

        // Create a fake operation (can be mocked too)
        val fakeOperation = mockk<BrandsAndProductsQuery>(relaxed = true)

        // Create a UUID (required by builder)
        val uuid = com.benasher44.uuid.uuid4()

        // ✅ Use ApolloResponse.Builder to create a real instance
        val apolloResponse = ApolloResponse.Builder(
            operation = fakeOperation,
            requestUuid = uuid,
            data = mockData
        ).build()

        // Mock the Apollo client to return this real response
        coEvery { apolloClient.query(any<BrandsAndProductsQuery>()).execute() } returns apolloResponse

        // Call the actual method under test
        val res = repository.getBrands().first()

        // Assert
        assertNotNull(res)
    }


    @Test
    fun `getBrandProducts should return data when successful`() = runTest {
        // Arrange
        val mockData = mockk<ProductsByCollectionIDQuery.Data>(relaxed = true)
        val uuid = com.benasher44.uuid.uuid4()

        // We need to match the real operation
        val query = ProductsByCollectionIDQuery("brandId")

        // Create a real ApolloResponse with builder
        val response = ApolloResponse.Builder(
            operation = query,
            requestUuid = uuid,
            data = mockData
        ).build()

        // Mock the Apollo client
        coEvery { apolloClient.query(query).execute() } returns response

        // Act
        val result = repository.getBrandProducts("brandId").first()

        // Assert
        assertNotNull(result)
        assertEquals(mockData, result)
    }

    @Test
    fun `getBrandProducts should return null when Apollo throws error`() = runTest {
        val query = ProductsByCollectionIDQuery("brandId")

        // Simulate exception on query
        coEvery { apolloClient.query(query).execute() } throws ApolloException("Network error")

        val result = repository.getBrandProducts("brandId").first()

        assertNull(result)
    }



}