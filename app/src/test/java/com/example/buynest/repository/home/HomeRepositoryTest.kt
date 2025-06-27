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
        val mockData = mockk<BrandsAndProductsQuery.Data>(relaxed = true)

        val fakeOperation = mockk<BrandsAndProductsQuery>(relaxed = true)

        val uuid = com.benasher44.uuid.uuid4()

        val apolloResponse = ApolloResponse.Builder(
            operation = fakeOperation,
            requestUuid = uuid,
            data = mockData
        ).build()

        coEvery { apolloClient.query(any<BrandsAndProductsQuery>()).execute() } returns apolloResponse

        val res = repository.getBrands().first()

        assertNotNull(res)
    }


    @Test
    fun `getBrandProducts should return data when successful`() = runTest {
        val mockData = mockk<ProductsByCollectionIDQuery.Data>(relaxed = true)
        val uuid = com.benasher44.uuid.uuid4()

        val query = ProductsByCollectionIDQuery("brandId")

        val response = ApolloResponse.Builder(
            operation = query,
            requestUuid = uuid,
            data = mockData
        ).build()

        coEvery { apolloClient.query(query).execute() } returns response

        val result = repository.getBrandProducts("brandId").first()

        assertNotNull(result)
        assertEquals(mockData, result)
    }

    @Test
    fun `getBrandProducts should return null when Apollo throws error`() = runTest {
        val query = ProductsByCollectionIDQuery("brandId")

        coEvery { apolloClient.query(query).execute() } throws ApolloException("Network error")

        val result = repository.getBrandProducts("brandId").first()

        assertNull(result)
    }



}