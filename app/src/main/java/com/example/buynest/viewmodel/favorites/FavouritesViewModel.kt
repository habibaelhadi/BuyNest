package com.example.buynest.viewmodel.favorites

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.repository.favorite.FavoriteRepo
import com.example.buynest.utils.AppConstants.KEY_CART_ID
import com.example.buynest.utils.SecureSharedPrefHelper
import com.example.buynest.viewmodel.cart.CartUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class FavouritesViewModel(
    val repo: FavoriteRepo,
    val cartUseCase: CartUseCase
): ViewModel() {
    private val _mutableFavorite = MutableStateFlow<List<String>>(emptyList())
    val favorite = _mutableFavorite
    private val _productDetails = MutableStateFlow<UiResponseState>(UiResponseState.Loading)
    val productDetails = _productDetails

    fun addToFavorite(productId: String) {
        viewModelScope.launch {
            val result = repo.addToFavorite(productId)
            if (result.isSuccess) {
                _mutableFavorite.value += productId
            }
        }
    }

    fun removeFromFavorite(productId: String) {
        viewModelScope.launch {
            val result = repo.removeFromFavorite(productId)
            if (result.isSuccess) {
                _mutableFavorite.value -= productId
            }
        }
    }

    fun getAllFavorites(){
        viewModelScope.launch {
            val result = repo.getAllFavorites()
            if (result.isSuccess) {
                _mutableFavorite.value = result.getOrDefault(emptyList())
                getProductsByIds(result.getOrDefault(emptyList()))
            }
        }
    }

    fun getProductsByIds(productId: List<String>){
        viewModelScope.launch {
            repo.getProductsByIds(productId).collect{
                try {
                    _productDetails.value = UiResponseState.Loading
                    if (it != null) {
                        _productDetails.value = UiResponseState.Success(it)
                    } else {
                        _productDetails.value = UiResponseState.Error("No data received.")
                    }
                }catch (e:Exception){
                    _productDetails.value = UiResponseState.Error(e.message.toString())
                }
            }
        }
    }

    suspend fun addToCart(variantId: String, quantity: Int) {
        val cartId = SecureSharedPrefHelper.getString(KEY_CART_ID)
        if (cartId != null) {
            val response = cartUseCase.addOrUpdateCartItem(cartId, variantId, quantity)
            if (response.hasErrors()) {
                Log.e("CartError", "Failed to add item: ${response.errors}")
            } else {
//                Log.i("CartSuccess", "Added item to cart: $cartId")
            }
        } else {
            Log.w("CartWarning", "No cart ID found. You may want to call createCart() first.")
        }
    }
}