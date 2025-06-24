package com.example.buynest.repository.order

import com.apollographql.apollo3.api.ApolloResponse
import com.example.buynest.admin.CompleteDraftOrderMutation
import com.example.buynest.admin.CreateDraftOrderMutation
import com.example.buynest.admin.GetOrdersByEmailQuery
import com.example.buynest.model.entity.OrderModel
import kotlinx.coroutines.flow.Flow

interface IOrderRepo {
    suspend fun draftOrder(variables: OrderModel): ApolloResponse<CreateDraftOrderMutation.Data>
    suspend fun completeDraftOrder(draftOrderID: String): ApolloResponse<CompleteDraftOrderMutation.Data>
    suspend fun getAllOrders(email: String): Flow<GetOrdersByEmailQuery.Data>
}