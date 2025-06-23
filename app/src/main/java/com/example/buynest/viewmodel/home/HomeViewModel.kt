package com.example.buynest.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.buynest.repository.home.IHomeRepository
import com.example.buynest.model.state.UiResponseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeViewModel (val repository: IHomeRepository) : ViewModel() {
    private val _mutableBrand = MutableStateFlow<UiResponseState>(UiResponseState.Loading)
    val brand = _mutableBrand

    fun getBrands() {
        viewModelScope.launch {
            repository.getBrands().collect {
                try {
                    _mutableBrand.value = UiResponseState.Loading
                    if (it != null) {
                        val products = it.products.edges.map { edge -> edge.node }
                        val brands = it.collections.nodes
                        _mutableBrand.value = UiResponseState.Success(Pair(brands, products))
                    } else {
                        _mutableBrand.value = UiResponseState.Error("No data received.")
                    }
                } catch (e: Exception) {
                    _mutableBrand.value = UiResponseState.Error(e.message.toString())
                }
            }
        }
    }
}

class HomeFactory(private val repo: IHomeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}