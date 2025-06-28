package com.example.buynest.utils

import com.example.buynest.model.mapper.countryToCurrencyCodeMap
import com.example.buynest.model.mapper.currencySymbolMap

object CurrencyHelper {

    fun getCurrencySymbol(code: String): String {
        return currencySymbolMap[code] ?: code
    }

    fun getCurrencyName(code: String): String {
        return countryToCurrencyCodeMap[code] ?: code
    }
}