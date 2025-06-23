package com.example.buynest.repository.order

import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.example.buynest.admin.CompleteDraftOrderMutation
import com.example.buynest.admin.CreateDraftOrderMutation
import com.example.buynest.mapper.toDraftOrderInput
import com.example.buynest.model.entity.OrderModel
import com.example.buynest.model.remote.graphql.ApolloAdmin.apolloAdmin

class OrderRepo : IOrderRepo {
    override suspend fun draftOrder(order: OrderModel): ApolloResponse<CreateDraftOrderMutation.Data> {
        val input = order.toDraftOrderInput()
        val mutation = CreateDraftOrderMutation(input)
        return apolloAdmin.mutation(mutation).execute()
    }

    override suspend fun completeDraftOrder(draftOrderID: String): ApolloResponse<CompleteDraftOrderMutation.Data> {
        val mutation = CompleteDraftOrderMutation(draftOrderID,
        )
        return apolloAdmin.mutation(mutation).execute()
    }

}