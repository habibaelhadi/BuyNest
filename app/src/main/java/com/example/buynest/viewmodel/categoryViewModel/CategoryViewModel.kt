package com.example.buynest.viewmodel.categoryViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.repository.category.ICategoryRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(val repo : ICategoryRepo):ViewModel() {
    private val _mutableCategoryProducts = MutableStateFlow<UiResponseState>(UiResponseState.Loading)
    var categoryProducts = _mutableCategoryProducts

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory = _selectedCategory

    private val _selectedSubcategory = MutableStateFlow<String?>(null)
    val selectedSubcategory = _selectedSubcategory

    fun setSelectedCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun setSelectedSubcategory(subcategory: String?) {
        _selectedSubcategory.value = subcategory
    }


    fun getCategoryProducts(categoryName: String) {
        viewModelScope.launch {
            _selectedCategory.value = categoryName
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
