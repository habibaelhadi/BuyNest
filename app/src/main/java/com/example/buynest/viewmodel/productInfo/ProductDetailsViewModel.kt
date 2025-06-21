package com.example.buynest.viewmodel.productInfo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.buynest.model.uistate.ResponseState
import com.example.buynest.repository.productDetails.ProductDetailsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ProductDetailsViewModel(val repository: ProductDetailsRepository): ViewModel() {

    private val _mutableProductDetails = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val productDetails = _mutableProductDetails

    fun getProductDetails(id: String) {
        viewModelScope.launch {
            repository.getProductDetails(id).collect {
                try {
                    _mutableProductDetails.value = ResponseState.Loading
                    if (it != null) {
                        _mutableProductDetails.value = ResponseState.Success(it)
                    } else {
                        _mutableProductDetails.value = ResponseState.Error("No data received.")
                    }

                }catch (e: Exception){
                    _mutableProductDetails.value = ResponseState.Error(e.message.toString())
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