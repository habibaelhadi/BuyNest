package com.example.buynest.repository.cart.datasource

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
        selectedOptions: List<Pair<String, String>>
    ): ApolloResponse<*> {
        val cartResult = apolloClient.query(GetCartQuery(cartId)).execute()

        val matchingLine = cartResult.data?.cart?.lines?.edges
            ?.mapNotNull { it?.node }
            ?.find { line ->
                val variant = line.merchandise.onProductVariant
                variant?.id == variantId &&
                        variant.selectedOptions
                            .map { it.name to it.value }
                            .toSet() == selectedOptions.toSet()
            }

        return if (matchingLine != null) {
            // Same variant and options → update quantity
            val updatedQuantity = matchingLine.quantity + quantity
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
            // New size or color → add as new variant
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