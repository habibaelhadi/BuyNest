package com.example.buynest.viewmodel.cart

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.ApolloResponse
import com.example.buynest.AddItemToCartMutation
import com.example.buynest.CreateCartMutation
import com.example.buynest.GetCartQuery
import com.example.buynest.LinkCartToCustomerMutation
import com.example.buynest.RemoveItemFromCartMutation
import com.example.buynest.admin.CreateDraftOrderMutation
import com.example.buynest.model.entity.AddressModel
import com.example.buynest.model.entity.CartItem
import com.example.buynest.model.entity.OrderModel
import com.example.buynest.repository.cart.CartRepository
import com.example.buynest.repository.order.IOrderRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel(
    private val repository: CartRepository,
    private val orderRepository: IOrderRepo
) : ViewModel() {

    private val _cartResponse = MutableStateFlow<ApolloResponse<GetCartQuery.Data>?>(null)
    val cartResponse: StateFlow<ApolloResponse<GetCartQuery.Data>?> = _cartResponse

    private val _createCartResponse =
        MutableStateFlow<ApolloResponse<CreateCartMutation.Data>?>(null)
    val createCartResponse: StateFlow<ApolloResponse<CreateCartMutation.Data>?> =
        _createCartResponse

    private val _addItemResponse =
        MutableStateFlow<ApolloResponse<AddItemToCartMutation.Data>?>(null)
    val addItemResponse: StateFlow<ApolloResponse<AddItemToCartMutation.Data>?> = _addItemResponse

    private val _removeItemResponse =
        MutableStateFlow<ApolloResponse<RemoveItemFromCartMutation.Data>?>(null)
    val removeItemResponse: StateFlow<ApolloResponse<RemoveItemFromCartMutation.Data>?> =
        _removeItemResponse

    private val _linkCartResponse =
        MutableStateFlow<ApolloResponse<LinkCartToCustomerMutation.Data>?>(null)
    val linkCartResponse: StateFlow<ApolloResponse<LinkCartToCustomerMutation.Data>?> =
        _linkCartResponse

    private val _orderResponse = MutableStateFlow<ApolloResponse<CreateDraftOrderMutation.Data>?>(null)
    val orderResponse: StateFlow<ApolloResponse<CreateDraftOrderMutation.Data>?> = _orderResponse


    fun getCart(cartId: String) {
        viewModelScope.launch {
            _cartResponse.value = repository.getCart(cartId)
        }
    }

    fun removeItemFromCart(cartId: String, lineId: String) {
        viewModelScope.launch {
            _removeItemResponse.value = repository.removeItem(cartId, lineId)
        }
    }

    fun getOrderModelFromCart(email: String, address: AddressModel?, items: List<CartItem>,isPaid: Boolean,discount: Double) {
        Log.i("TAG", "getOrderModelFromCart: email is $email address is $address and items is $items")
        val order =  OrderModel(
            email = email,
            address = address!!,
            orderItems = items,
            isPaid = isPaid,
            discount = discount
        )
        draftOrder(order)

    }

    fun draftOrder(order: OrderModel){
        viewModelScope.launch {
            val response = orderRepository.draftOrder(order)
            _orderResponse.value = response

            val draftOrder = response.data?.draftOrderCreate?.draftOrder
            val errors = response.data?.draftOrderCreate?.userErrors

            if (draftOrder != null) {
                Log.i("TAG", "Draft order created: ${draftOrder.id}")
            } else {
                Log.e("TAG", "Draft order is null")
                errors?.forEach {
                    Log.e("TAG", "Error: ${it.field} - ${it.message}")
                }
            }
        }
    }

    fun completeOrder(draftOrderId: String){
        viewModelScope.launch {
            orderRepository.completeDraftOrder(draftOrderId)
        }
    }
}