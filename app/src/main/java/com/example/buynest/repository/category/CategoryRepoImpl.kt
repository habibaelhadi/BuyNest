package com.example.buynest.repository.category

import com.apollographql.apollo3.ApolloClient
import com.example.buynest.ProductsByHandleQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
class CategoryRepoImpl (
    private val apolloClient: ApolloClient
): ICategoryRepo {
    override fun getProductByCategoryName(categoryName: String): Flow<ProductsByHandleQuery.Data?> =
        flow {
            val response = apolloClient
                .query(ProductsByHandleQuery(categoryName))
                .execute()
            emit(response.data)
        }.catch {
            emit(null)
        }
}