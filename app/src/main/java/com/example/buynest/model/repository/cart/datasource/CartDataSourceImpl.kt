package com.example.buynest.model.repository.cart.datasource

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.example.buynest.AddItemToCartMutation
import com.example.buynest.CreateCartMutation
import com.example.buynest.GetCartQuery
import com.example.buynest.LinkCartToCustomerMutation
import com.example.buynest.RemoveItemFromCartMutation
import com.example.buynest.type.CartLineInput

class CartDataSourceImpl(
    private val apolloClient: ApolloClient,
): CartDataSource {
    override suspend fun createCart(): ApolloResponse<CreateCartMutation.Data> {
        return apolloClient.mutation(CreateCartMutation()).execute()
    }

    override suspend fun linkToCustomer(cartId: String, token: String): ApolloResponse<LinkCartToCustomerMutation.Data> {
        return apolloClient.mutation(
            LinkCartToCustomerMutation(cartId = cartId, customerAccessToken = token)
        ).execute()
    }

    override suspend fun getCart(cartId: String): ApolloResponse<GetCartQuery.Data> {
        return apolloClient.query(GetCartQuery(cartId)).execute()
    }

    override suspend fun addItemToCart(
        cartId: String,
        variantId: String,
        quantity: Int
    ): ApolloResponse<*> {
        Log.i("CartAdd", "Adding variantId=$variantId with qty=$quantity to cart $cartId")

        val lineInput = CartLineInput(
            merchandiseId = variantId,
            quantity = Optional.Present(quantity)
        )

        return apolloClient.mutation(
            AddItemToCartMutation(
                cartId = cartId,
                lines = listOf(lineInput)
            )
        ).execute()
    }



    override suspend fun removeItem(cartId: String, lineId: String): ApolloResponse<RemoveItemFromCartMutation.Data> {
        return apolloClient.mutation(RemoveItemFromCartMutation(cartId, listOf(lineId))).execute()
    }
}