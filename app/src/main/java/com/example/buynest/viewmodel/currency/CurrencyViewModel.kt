package com.example.buynest.viewmodel.currency

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.*
import com.example.buynest.repository.currency.ICurrencyRepository
import com.example.buynest.utils.CurrencyHelper.getCurrencyName
import com.example.buynest.utils.CurrencyHelper.getCurrencySymbol
import com.example.buynest.utils.SharedPrefHelper

import kotlinx.coroutines.launch

class CurrencyViewModel(
    private val repository: ICurrencyRepository,
    private val context: Context
) : ViewModel() {

    private val _rate = mutableDoubleStateOf(0.0)
    val rate: State<Double> get() = _rate

    private val _currencySymbol = mutableStateOf<String?>(null)
    val currencySymbol: State<String?> get() = _currencySymbol


     fun loadCurrency(base: String = "EGP") {
        viewModelScope.launch {
            val result = repository.getCurrencyRates(base = base, context = context)
            val targetCurrency = getCurrencyName(SharedPrefHelper.getCurrency(context))
            _rate.value = result?.rates?.get(targetCurrency) ?: 0.0
            _currencySymbol.value = getCurrencySymbol(targetCurrency).toString()
        }
    }
}
