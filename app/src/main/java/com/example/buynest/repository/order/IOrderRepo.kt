package com.example.buynest.repository.order

import com.apollographql.apollo3.api.ApolloResponse
import com.example.buynest.admin.CompleteDraftOrderMutation
import com.example.buynest.admin.CreateDraftOrderMutation
import com.example.buynest.model.entity.OrderModel

interface IOrderRepo {
    suspend fun draftOrder(variables: OrderModel): ApolloResponse<CreateDraftOrderMutation.Data>
    suspend fun completeDraftOrder(draftOrderID: String): ApolloResponse<CompleteDraftOrderMutation.Data>
}