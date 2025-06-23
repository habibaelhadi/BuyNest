package com.example.buynest.viewmodel.cart

import com.apollographql.apollo3.api.ApolloResponse
import com.example.buynest.AddItemToCartMutation
import com.example.buynest.CreateCartMutation
import com.example.buynest.LinkCartToCustomerMutation
import com.example.buynest.repository.cart.CartRepository

object CartManager {
    private lateinit var repository: CartRepository

    fun setup(repo: CartRepository) {
        repository = repo
    }

    suspend fun createCart(): ApolloResponse<CreateCartMutation.Data> {
        return repository.createCart()
    }

    suspend fun linkCartToCustomer(cartId: String, token: String): ApolloResponse<LinkCartToCustomerMutation.Data>{
        return repository.linkCart(cartId, token)
    }

    suspend fun addItemToCart(cartId: String, variantId: String, quantity: Int, selectedOptions: List<Pair<String, String>>): ApolloResponse<*> {
        return repository.addOrUpdateItem(cartId, variantId, quantity, selectedOptions)
    }

}
