package com.example.buynest.viewmodel.productInfo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.buynest.model.data.remote.graphql.ApolloClient.apolloClient
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.repository.cart.CartRepositoryImpl
import com.example.buynest.repository.cart.datasource.CartDataSourceImpl
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

    suspend fun addToCart(variantId: String, quantity: Int, selectedOptions: List<Pair<String, String>>) {
        val cartId = SecureSharedPrefHelper.getString(KEY_CART_ID)
        if (cartId != null) {
            CartManager.setup(CartRepositoryImpl(cartDataSource = CartDataSourceImpl(apolloClient)))
            val response = CartManager.addItemToCart(cartId, variantId, quantity, selectedOptions)
            if (response.hasErrors()) {
                Log.e("CartError", "Failed to add item from product details: ${response.errors}")
            } else {
                Log.i("CartSuccess", "Added item to cart from product details : $cartId")
            }
        } else {
            Log.w("CartWarning", "No cart ID found. You may want to call createCart() first.")
        }
    }

    class ProductInfoFactory(private val repository: ProductDetailsRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProductDetailsViewModel(repository) as T
        }
    }
}