package com.example.buynest.viewmodel.brandproducts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.buynest.repository.homeRepository.IHomeRepository
import com.example.buynest.model.uistate.ResponseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BrandDetailsViewModel (val repo : IHomeRepository): ViewModel() {
    private val _mutableBrandProducts = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val brandProducts = _mutableBrandProducts

    fun getBrandProducts(Id: String) {
        viewModelScope.launch {
            repo.getBrandProducts(Id).collect {
                try{
                    _mutableBrandProducts.value = ResponseState.Loading
                    if(it != null){
                        _mutableBrandProducts.value = ResponseState.Success(it)
                    }else{
                        _mutableBrandProducts.value = ResponseState.Error("No data received.")
                    }
                }catch(e : Exception){
                    _mutableBrandProducts.value = ResponseState.Error(e.message.toString())
                }
            }
        }
    }


}



class BrandProductsFactory(private val repo: IHomeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BrandDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BrandDetailsViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}