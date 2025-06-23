package com.example.buynest.repository.cart

import com.apollographql.apollo3.api.ApolloResponse
import com.example.buynest.AddItemToCartMutation
import com.example.buynest.CreateCartMutation
import com.example.buynest.GetCartQuery
import com.example.buynest.LinkCartToCustomerMutation
import com.example.buynest.RemoveItemFromCartMutation

interface CartRepository {
    suspend fun createCart(): ApolloResponse<CreateCartMutation.Data>
    suspend fun linkCart(cartId: String, token: String): ApolloResponse<LinkCartToCustomerMutation.Data>
    suspend fun getCart(cartId: String): ApolloResponse<GetCartQuery.Data>
    suspend fun addOrUpdateCartItem(cartId: String, variantId: String, quantity: Int, selectedSize: String?, selectedColor: String?): ApolloResponse<*>
    suspend fun removeItem(cartId: String, lineId: String): ApolloResponse<RemoveItemFromCartMutation.Data>
  //  suspend fun updateQuantity(cartId: String, lineId: String, quantity: Int): ApolloResponse<UpdateCartItemQuantityMutation.Data>
}