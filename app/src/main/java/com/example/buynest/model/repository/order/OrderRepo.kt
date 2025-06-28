package com.example.buynest.model.repository.order

import android.util.Log
import com.apollographql.apollo3.api.ApolloResponse
import com.example.buynest.admin.CompleteDraftOrderMutation
import com.example.buynest.admin.CreateDraftOrderMutation
import com.example.buynest.admin.GetOrdersByEmailQuery
import com.example.buynest.model.mapper.toDraftOrderInput
import com.apollographql.apollo3.ApolloClient
import com.example.buynest.model.entity.OrderModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class OrderRepo(
    private val apolloClient: ApolloClient
) : IOrderRepo {

    override suspend fun draftOrder(order: OrderModel): ApolloResponse<CreateDraftOrderMutation.Data> {
        val input = order.toDraftOrderInput()
        val mutation = CreateDraftOrderMutation(input)
        return apolloClient.mutation(mutation).execute()
    }

    override suspend fun completeDraftOrder(draftOrderID: String): ApolloResponse<CompleteDraftOrderMutation.Data> {
        val mutation = CompleteDraftOrderMutation(draftOrderID)
        return apolloClient.mutation(mutation).execute()
    }

    override suspend fun getAllOrders(email: String): Flow<GetOrdersByEmailQuery.Data> = flow {
        val query = GetOrdersByEmailQuery(email)
        val response = apolloClient.query(query).execute()
        emit(response.data!!)
    }.catch {
        Log.e("TAG", "getAllOrders: ${it.message} ")
    }
}