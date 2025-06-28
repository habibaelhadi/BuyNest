package com.example.buynest.viewmodel.brandproducts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.model.repository.home.IHomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BrandDetailsViewModel(val repo: IHomeRepository) : ViewModel() {
    private val _mutableBrandProducts = MutableStateFlow<UiResponseState>(UiResponseState.Loading)
    val brandProducts = _mutableBrandProducts

    fun getBrandProducts(Id: String) {
        viewModelScope.launch {
            _mutableBrandProducts.value = UiResponseState.Loading
            try {
                repo.getBrandProducts(Id).collect { data ->
                    _mutableBrandProducts.value = if (data != null) {
                        UiResponseState.Success(data)
                    } else {
                        UiResponseState.Error("No data received.")
                    }
                }
            } catch (e: Exception) {
                _mutableBrandProducts.value = UiResponseState.Error(e.message.toString())
            }
        }
    }
}

