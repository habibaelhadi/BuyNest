package com.example.buynest.repository.cart

import com.apollographql.apollo3.api.ApolloResponse
import com.example.buynest.AddItemToCartMutation
import com.example.buynest.CreateCartMutation
import com.example.buynest.GetCartQuery
import com.example.buynest.LinkCartToCustomerMutation
import com.example.buynest.RemoveItemFromCartMutation
import com.example.buynest.repository.cart.datasource.CartDataSource

class CartRepositoryImpl(
    private val cartDataSource: CartDataSource,
): CartRepository {
    override suspend fun createCart(): ApolloResponse<CreateCartMutation.Data> =
        cartDataSource.createCart()

    override suspend fun linkCart(cartId: String, token: String): ApolloResponse<LinkCartToCustomerMutation.Data> =
        cartDataSource.linkToCustomer(cartId, token)

    override suspend fun getCart(cartId: String): ApolloResponse<GetCartQuery.Data> =
        cartDataSource.getCart(cartId)

    override suspend fun addOrUpdateCartItem(cartId: String, variantId: String, quantity: Int, selectedSize: String?, selectedColor: String?): ApolloResponse<*> =
        cartDataSource.addOrUpdateItem(cartId, variantId, quantity, selectedSize, selectedColor)

    override suspend fun removeItem(cartId: String, lineId: String): ApolloResponse<RemoveItemFromCartMutation.Data> =
        cartDataSource.removeItem(cartId, lineId)

//    override suspend fun updateQuantity(cartId: String, lineId: String, quantity: Int): ApolloResponse<UpdateCartItemQuantityMutation.Data> =
//        cartDataSource.updateQuantity(cartId, lineId, quantity)

}