package com.example.buynest.model.remote.repository

import com.example.buynest.BrandsAndProductsQuery
import com.example.buynest.model.remote.graphql.ApolloClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class HomeRepository : IHomeRepository {
    override fun getBrands(): Flow<BrandsAndProductsQuery.Data?> = flow {
        val response = ApolloClient.apolloClient
            .query(BrandsAndProductsQuery())
            .execute()

        emit(response.data)
    }.catch {
        emit(null)
    }

    override fun getBrandProducts(): Flow<BrandsAndProductsQuery.Data?>  = flow{
        val response = ApolloClient.apolloClient
            .query(BrandsAndProductsQuery())
            .execute()
        emit(response.data)
    }.catch{
        emit(null)
    }
}