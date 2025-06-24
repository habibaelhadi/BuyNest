package com.example.buynest.repository.order

import android.util.Log
import com.apollographql.apollo3.api.ApolloResponse
import com.example.buynest.admin.CompleteDraftOrderMutation
import com.example.buynest.admin.CreateDraftOrderMutation
import com.example.buynest.mapper.toDraftOrderInput
import com.example.buynest.model.entity.OrderModel
import com.example.buynest.model.remote.graphql.ApolloAdmin.apolloAdmin

class OrderRepo : IOrderRepo {
    override suspend fun draftOrder(order: OrderModel): ApolloResponse<CreateDraftOrderMutation.Data> {
        val input = order.toDraftOrderInput()
        Log.e("TAG", "repo draftOrder: $input")
        val mutation = CreateDraftOrderMutation(input)
        Log.i("TAG", "repo draftOrder: $mutation")
        order.orderItems.forEach {
            Log.d("TAG", "Item: ${it.name}, variantId: '${it.variantId}'")
        }
        return apolloAdmin.mutation(mutation).execute()
    }

    override suspend fun completeDraftOrder(draftOrderID: String): ApolloResponse<CompleteDraftOrderMutation.Data> {
        val mutation = CompleteDraftOrderMutation(draftOrderID,
        )
        return apolloAdmin.mutation(mutation).execute()
    }

}