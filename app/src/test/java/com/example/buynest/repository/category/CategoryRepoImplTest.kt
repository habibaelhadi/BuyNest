package com.example.buynest.repository.category

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.exception.ApolloException
import com.example.buynest.ProductsByHandleQuery
import com.example.buynest.model.repository.category.CategoryRepoImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull


class CategoryRepoImplTest {
    private lateinit var apolloClient: ApolloClient
    private lateinit var repo: CategoryRepoImpl

    @Before
    fun setup() {
        apolloClient = mockk(relaxed = true)
        repo = CategoryRepoImpl(apolloClient)
    }

    @Test
    fun `getProductByCategoryName should return data when successful`() = runTest {
        val fakeCategoryName = "electronics"
        val mockData = mockk<ProductsByHandleQuery.Data>(relaxed = true)
        val query = ProductsByHandleQuery(fakeCategoryName)
        val uuid = com.benasher44.uuid.uuid4()

        val response = ApolloResponse.Builder(
            operation = query,
            requestUuid = uuid,
            data = mockData
        ).build()

        coEvery { apolloClient.query(query).execute() } returns response

        val result = repo.getProductByCategoryName(fakeCategoryName).first()

        assertNotNull(result)
        assertEquals(mockData, result)
    }

    @Test
    fun `getProductByCategoryName should emit null when exception is thrown`() = runTest {
        val fakeCategoryName = "invalid"

        val query = ProductsByHandleQuery(fakeCategoryName)
        coEvery { apolloClient.query(query).execute() } throws ApolloException("Network failure")

        val result = repo.getProductByCategoryName(fakeCategoryName).first()

        assertNull(result)
    }
}