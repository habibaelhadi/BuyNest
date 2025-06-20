package com.example.buynest.viewmodel.favorites

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.buynest.model.uistate.ResponseState
import com.example.buynest.repository.favoriteRepo.FavoriteRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class FavouritesViewModel(val repo: FavoriteRepo): ViewModel() {
    private val _mutableFavorite = MutableStateFlow<List<String>>(emptyList())
    val favorite = _mutableFavorite
    private val _productDetails = MutableStateFlow<ResponseState>(ResponseState.Loading)
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
        Log.i("TAG", "getProductsByIds: $productId")
        viewModelScope.launch {
            repo.getProductsByIds(productId).collect{
                try {
                    _productDetails.value = ResponseState.Loading
                    if (it != null) {
                        _productDetails.value = ResponseState.Success(it)
                    } else {
                        _productDetails.value = ResponseState.Error("No data received.")
                    }
                }catch (e:Exception){
                    _productDetails.value = ResponseState.Error(e.message.toString())
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class FavouritesFactory(private val repo: FavoriteRepo): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FavouritesViewModel(repo) as T
        }
    }
}