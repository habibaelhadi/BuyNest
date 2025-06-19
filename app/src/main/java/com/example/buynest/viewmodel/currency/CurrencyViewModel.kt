package com.example.buynest.viewmodel.currency

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableDoubleStateOf
import com.example.buynest.repository.currency.ICurrencyRepository

class CurrencyViewModel(
    private val repository: ICurrencyRepository,
    private val context: Context
) : ViewModel() {

    private val _rate = mutableDoubleStateOf(0.0)
    val rate: State<Double> get() = _rate

    init {
        viewModelScope.launch {
            _rate.value = repository.getExchangeRate(context)
        }
    }
}
