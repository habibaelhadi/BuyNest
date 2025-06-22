package com.example.buynest.viewmodel.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.ApolloResponse
import com.example.buynest.AddItemToCartMutation
import com.example.buynest.CreateCartMutation
import com.example.buynest.GetCartQuery
import com.example.buynest.LinkCartToCustomerMutation
import com.example.buynest.RemoveItemFromCartMutation
import com.example.buynest.repository.cart.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel(
    private val repository: CartRepository
) : ViewModel() {

    private val _cartResponse = MutableStateFlow<ApolloResponse<GetCartQuery.Data>?>(null)
    val cartResponse: StateFlow<ApolloResponse<GetCartQuery.Data>?> = _cartResponse

    private val _createCartResponse = MutableStateFlow<ApolloResponse<CreateCartMutation.Data>?>(null)
    val createCartResponse: StateFlow<ApolloResponse<CreateCartMutation.Data>?> = _createCartResponse

    private val _addItemResponse = MutableStateFlow<ApolloResponse<AddItemToCartMutation.Data>?>(null)
    val addItemResponse: StateFlow<ApolloResponse<AddItemToCartMutation.Data>?> = _addItemResponse

    private val _removeItemResponse = MutableStateFlow<ApolloResponse<RemoveItemFromCartMutation.Data>?>(null)
    val removeItemResponse: StateFlow<ApolloResponse<RemoveItemFromCartMutation.Data>?> = _removeItemResponse

    private val _linkCartResponse = MutableStateFlow<ApolloResponse<LinkCartToCustomerMutation.Data>?>(null)
    val linkCartResponse: StateFlow<ApolloResponse<LinkCartToCustomerMutation.Data>?> = _linkCartResponse

    fun createCart() {
        viewModelScope.launch {
            _createCartResponse.value = repository.createCart()
        }
    }

    fun linkCartToCustomer(cartId: String, token: String) {
        viewModelScope.launch {
            _linkCartResponse.value = repository.linkCart(cartId, token)
        }
    }

    fun getCart(cartId: String) {
        viewModelScope.launch {
            _cartResponse.value = repository.getCart(cartId)
        }
    }

    fun addItemToCart(cartId: String, variantId: String, quantity: Int) {
        viewModelScope.launch {
            _addItemResponse.value = repository.addItem(cartId, variantId, quantity)
        }
    }

    fun removeItemFromCart(cartId: String, lineId: String) {
        viewModelScope.launch {
            _removeItemResponse.value = repository.removeItem(cartId, lineId)
        }
    }
}
