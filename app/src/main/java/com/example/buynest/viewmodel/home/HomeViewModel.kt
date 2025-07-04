package com.example.buynest.viewmodel.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buynest.model.repository.home.IHomeRepository
import com.example.buynest.model.state.UiResponseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeViewModel (val repository: IHomeRepository) : ViewModel() {
    private val _mutableBrand = MutableStateFlow<UiResponseState>(UiResponseState.Loading)
    val brand = _mutableBrand

    fun getBrands(context: Context) {
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