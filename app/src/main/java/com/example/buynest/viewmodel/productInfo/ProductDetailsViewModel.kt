package com.example.buynest.viewmodel.productInfo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.ApolloResponse
import com.example.buynest.CreateCartMutation
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.repository.productDetails.ProductDetailsRepository
import com.example.buynest.utils.AppConstants.KEY_CART_ID
import com.example.buynest.utils.SecureSharedPrefHelper
import com.example.buynest.viewmodel.cart.CartManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ProductDetailsViewModel(val repository: ProductDetailsRepository): ViewModel() {

    private val _mutableProductDetails = MutableStateFlow<UiResponseState>(UiResponseState.Loading)
    val productDetails = _mutableProductDetails

    fun getProductDetails(id: String) {
        viewModelScope.launch {
            repository.getProductDetails(id).collect {
                try {
                    _mutableProductDetails.value = UiResponseState.Loading
                    if (it != null) {
                        _mutableProductDetails.value = UiResponseState.Success(it)
                    } else {
                        _mutableProductDetails.value = UiResponseState.Error("No data received.")
                    }

                }catch (e: Exception){
                    _mutableProductDetails.value = UiResponseState.Error(e.message.toString())
                }
            }
        }
    }

    suspend fun addToCart(variantId: String, quantity: Int) {
        var cartId = SecureSharedPrefHelper.getString(KEY_CART_ID)

        Log.i("CartInfo from pd_viewmodel", "Cart ID: $cartId")

        if (cartId == null) {
            Log.i("CartInfo", "No cart ID found. Creating new cart.")
            val createResponse: ApolloResponse<CreateCartMutation.Data> = CartManager.createCart()
            cartId = createResponse.data?.cartCreate?.cart?.id

            if (cartId == null) {
                Log.e("CartError", "Failed to create cart.")
                return
            }

            SecureSharedPrefHelper.putString(KEY_CART_ID, cartId)
        }
        val response = CartManager.addOrUpdateCartItem(cartId, variantId, quantity)
        if (response.hasErrors()) {
            Log.e("CartError", "Failed to add item: ${response.errors}")
        } else {
            Log.i("CartSuccess", "Item added to cart: $variantId in cart $cartId")
        }
    }

    class ProductInfoFactory(private val repository: ProductDetailsRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProductDetailsViewModel(repository) as T
        }
    }
}