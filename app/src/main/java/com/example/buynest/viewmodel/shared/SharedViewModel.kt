package com.example.buynest.viewmodel.shared

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.buynest.BrandsAndProductsQuery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedViewModel : ViewModel() {
    private val _mutableCategory = MutableStateFlow<List<BrandsAndProductsQuery.Node3>>(emptyList())
    val category = _mutableCategory.asStateFlow()

    fun setCategories(categories: List<BrandsAndProductsQuery.Node3>) {
        if (categories.isNotEmpty()) {
            _mutableCategory.value = categories
        }
    }
}