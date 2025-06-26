package com.example.buynest.repository.category

import com.example.buynest.BuildConfig
import com.example.buynest.ProductsByHandleQuery
import com.example.buynest.model.data.remote.graphql.ApolloClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import com.example.buynest.utils.constant.*
class CategoryRepoImpl: ICategoryRepo {
    override fun getProductByCategoryName(categoryName: String): Flow<ProductsByHandleQuery.Data?> =
        flow {
            val response = ApolloClient.createApollo(
                BASE_URL = CLIENT_BASE_URL,
                ACCESS_TOKEN = BuildConfig.SHOPIFY_ACCESS_TOKEN,
                Header = CLIENT_HEADER
            )
                .query(ProductsByHandleQuery(categoryName))
                .execute()
            emit(response.data)
        }.catch {
            emit(null)
        }
}