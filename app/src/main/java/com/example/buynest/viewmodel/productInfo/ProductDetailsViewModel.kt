package com.example.buynest.viewmodel.productInfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.repository.productDetails.ProductDetailsRepository
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

    class ProductInfoFactory(private val repository: ProductDetailsRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProductDetailsViewModel(repository) as T
        }
    }
}