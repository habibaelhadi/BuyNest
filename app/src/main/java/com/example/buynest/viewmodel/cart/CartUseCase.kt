package com.example.buynest.viewmodel.cart

import com.apollographql.apollo3.api.ApolloResponse
import com.example.buynest.CreateCartMutation
import com.example.buynest.LinkCartToCustomerMutation
import com.example.buynest.repository.cart.CartRepository

class CartUseCase(private val repository: CartRepository) {

    suspend fun createCart(): ApolloResponse<CreateCartMutation.Data> {
        return repository.createCart()
    }

    suspend fun linkCartToCustomer(cartId: String, token: String): ApolloResponse<LinkCartToCustomerMutation.Data> {
        return repository.linkCart(cartId, token)
    }

    suspend fun addOrUpdateCartItem(cartId: String, variantId: String, quantity: Int): ApolloResponse<*> {
        return repository.addItemToCart(cartId, variantId, quantity)
    }
}
