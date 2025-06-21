package com.example.buynest.viewmodel.categoryViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.buynest.model.uistate.ResponseState
import com.example.buynest.repository.categoryrepo.ICategoryRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(val repo : ICategoryRepo):ViewModel() {
   private val _mutableCategoryProducts = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val categoryProducts = _mutableCategoryProducts

    fun getCategoryProducts(categoryName:String) {
        viewModelScope.launch {
            repo.getProductByCategoryName(categoryName).collect {
                try {
                    if (it != null) {
                        _mutableCategoryProducts.value = ResponseState.Success(it)
                    } else {
                        _mutableCategoryProducts.value = ResponseState.Error("Something went wrong")
                    }
                } catch (e: Exception) {
                    _mutableCategoryProducts.value = ResponseState.Error(e.message.toString())
                }
            }
        }
    }
}

class CategoryFactory(private val repo: ICategoryRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}