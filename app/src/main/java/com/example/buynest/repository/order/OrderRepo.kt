package com.example.buynest.repository.order

import android.util.Log
import com.apollographql.apollo3.api.ApolloResponse
import com.example.buynest.BuildConfig
import com.example.buynest.admin.CompleteDraftOrderMutation
import com.example.buynest.admin.CreateDraftOrderMutation
import com.example.buynest.admin.GetOrdersByEmailQuery
import com.example.buynest.mapper.toDraftOrderInput
import com.example.buynest.model.data.remote.graphql.ApolloClient
import com.example.buynest.model.entity.OrderModel
import com.example.buynest.utils.constant.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class OrderRepo : IOrderRepo {
    override suspend fun draftOrder(order: OrderModel): ApolloResponse<CreateDraftOrderMutation.Data> {
        val input = order.toDraftOrderInput()
        Log.e("TAG", "repo draftOrder: $input")
        val mutation = CreateDraftOrderMutation(input)
        Log.i("TAG", "repo draftOrder: $mutation")
        order.orderItems.forEach {
            Log.d("TAG", "Item: ${it.name}, variantId: '${it.variantId}'")
        }
         val response = ApolloClient.createApollo(
            BASE_URL = ADMIN_BASE_URL,
            ACCESS_TOKEN = BuildConfig.Admin_ACCESS_TOKEN,
            Header = ADMIN_HEADER
        ).mutation(mutation).execute()
        return response
    }

    override suspend fun completeDraftOrder(draftOrderID: String): ApolloResponse<CompleteDraftOrderMutation.Data> {
        val mutation = CompleteDraftOrderMutation(draftOrderID,
        )
         val response = ApolloClient.createApollo(
            BASE_URL = ADMIN_BASE_URL,
            ACCESS_TOKEN = BuildConfig.Admin_ACCESS_TOKEN,
            Header = ADMIN_HEADER
        ).mutation(mutation).execute()
        return response
    }

    override suspend fun getAllOrders(email: String): Flow<GetOrdersByEmailQuery.Data> = flow {
        val query = GetOrdersByEmailQuery(email)
        val response = ApolloClient.createApollo(
            BASE_URL = ADMIN_BASE_URL,
            ACCESS_TOKEN = BuildConfig.Admin_ACCESS_TOKEN,
            Header = ADMIN_HEADER
        ).query(query).execute()
        emit(response.data!!)
    }.catch {
        Log.e("TAG", "getAllOrders: ${it.message} ")
    }

}