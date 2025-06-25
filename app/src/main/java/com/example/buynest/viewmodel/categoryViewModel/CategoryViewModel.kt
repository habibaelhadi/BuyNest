package com.example.buynest.viewmodel.categoryViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.repository.category.ICategoryRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(val repo : ICategoryRepo):ViewModel() {
   private val _mutableCategoryProducts = MutableStateFlow<UiResponseState>(UiResponseState.Loading)
    val categoryProducts = _mutableCategoryProducts

    fun getCategoryProducts(categoryName:String) {
        viewModelScope.launch {
            repo.getProductByCategoryName(categoryName).collect {
                try {
                    if (it != null) {
                        _mutableCategoryProducts.value = UiResponseState.Success(it)
                    } else {
                        _mutableCategoryProducts.value = UiResponseState.Error("Something went wrong")
                    }
                } catch (e: Exception) {
                    _mutableCategoryProducts.value = UiResponseState.Error(e.message.toString())
                }
            }
        }
    }
}
