package com.example.buynest.repository.cart.datasource

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.example.buynest.AddItemToCartMutation
import com.example.buynest.CartLinesUpdateMutation
import com.example.buynest.CreateCartMutation
import com.example.buynest.GetCartQuery
import com.example.buynest.LinkCartToCustomerMutation
import com.example.buynest.RemoveItemFromCartMutation
import com.example.buynest.type.CartLineInput
import com.example.buynest.type.CartLineUpdateInput

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

    override suspend fun addOrUpdateItem(
        cartId: String,
        variantId: String,
        quantity: Int,
        selectedSize: String?,
        selectedColor: String?
    ): ApolloResponse<*> {
        Log.d("CartOperation", "Fetching cart for ID: $cartId")

        val cartResult = apolloClient.query(GetCartQuery(cartId)).execute()

        Log.d("CartOperation", "Looking for matching line with size=$selectedSize and color=$selectedColor")

        val matchingLine = cartResult.data?.cart?.lines?.edges
            ?.mapNotNull { it?.node }
            ?.onEach { line ->
                val variant = line.merchandise.onProductVariant
                val options = variant?.selectedOptions?.associate { it.name.lowercase() to it.value }

                Log.d("CartDebug", "Found line: variantId=${variant?.id}, options=$options, quantity=${line.quantity}")
            }
            ?.find { line ->
                val variant = line.merchandise.onProductVariant ?: return@find false
                val options = variant.selectedOptions.associate { it.name.lowercase() to it.value }

                options["size"] == selectedSize && options["color"] == selectedColor
            }

        return if (matchingLine != null) {
            val updatedQuantity = matchingLine.quantity + quantity
            Log.i("CartUpdate", "Updating quantity for matching line: id=${matchingLine.id}, newQty=$updatedQuantity")

            val updateInput = CartLineUpdateInput(
                id = matchingLine.id,
                quantity = Optional.Present(updatedQuantity)
            )

            apolloClient.mutation(
                CartLinesUpdateMutation(
                    cartId = cartId,
                    lines = listOf(updateInput)
                )
            ).execute()
        } else {
            Log.i("CartAdd", "No matching line found. Adding new variantId=$variantId with qty=$quantity")

            val lineInput = CartLineInput(
                merchandiseId = variantId,
                quantity = Optional.Present(quantity)
            )

            apolloClient.mutation(
                AddItemToCartMutation(
                    cartId = cartId,
                    lines = listOf(lineInput)
                )
            ).execute()
        }
    }


    override suspend fun removeItem(cartId: String, lineId: String): ApolloResponse<RemoveItemFromCartMutation.Data> {
        return apolloClient.mutation(RemoveItemFromCartMutation(cartId, listOf(lineId))).execute()
    }
}