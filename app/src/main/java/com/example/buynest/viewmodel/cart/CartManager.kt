package com.example.buynest.viewmodel.cart

import com.apollographql.apollo3.api.ApolloResponse
import com.example.buynest.CreateCartMutation
import com.example.buynest.LinkCartToCustomerMutation
import com.example.buynest.repository.cart.CartRepository

object CartManager {
    private var repository: CartRepository? = null

    fun setup(repo: CartRepository) {
        repository = repo
    }

    private fun getRepo(): CartRepository {
        return repository ?: throw IllegalStateException("CartManager is not initialized. Call setup() first.")
    }

    suspend fun createCart(): ApolloResponse<CreateCartMutation.Data> {
        return getRepo().createCart()
    }

    suspend fun linkCartToCustomer(cartId: String, token: String): ApolloResponse<LinkCartToCustomerMutation.Data>{
        return getRepo().linkCart(cartId, token)
    }

    suspend fun addOrUpdateCartItem(cartId: String, variantId: String, quantity: Int): ApolloResponse<*> {
        return getRepo().addItemToCart(cartId, variantId, quantity)
    }

}
