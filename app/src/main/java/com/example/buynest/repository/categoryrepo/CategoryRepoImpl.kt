package com.example.buynest.repository.categoryrepo

import com.example.buynest.ProductsByHandleQuery
import com.example.buynest.model.remote.graphql.ApolloClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class CategoryRepoImpl: ICategoryRepo {
    override fun getProductByCategoryName(categoryName: String): Flow<ProductsByHandleQuery.Data?> =
        flow {
            val response = ApolloClient.apolloClient
                .query(ProductsByHandleQuery(categoryName))
                .execute()
            emit(response.data)
        }.catch {
            emit(null)
        }
}