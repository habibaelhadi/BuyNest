package com.example.buynest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.buynest.repository.favoriteRepo.FavoriteRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class FavouritesViewModel(val repo: FavoriteRepo): ViewModel() {
    private val _mutableFavorite = MutableStateFlow<List<String> >(emptyList())
    val favorite = _mutableFavorite


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