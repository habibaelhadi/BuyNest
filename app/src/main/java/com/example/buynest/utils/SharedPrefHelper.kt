package com.example.buynest.utils

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object SharedPrefHelper {
    private const val APP_PREFS = "app_prefs"
    private const val LAST_SEEN = "skip_splash"
    private const val LOGGED_IN = "logged_in"
    private const val AUTHENTICATION_MODE = "authentication_mode"
    private const val PAYMENT_KEY = "payment_method"
    private const val DEFAULT_METHOD = "Credit Card"
    private const val COUNTRY_KEY = "country"
    private const val DEFAULT_COUNTRY = "Egypt"
    private const val CURRENCY_KEY = "currency"
    private const val DEFAULT_CURRENCY = "EGP"

    @RequiresApi(Build.VERSION_CODES.O)
    fun setLastSeen(context: Context) {
        val prefs = context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE)
        val currentDate = LocalDate.now()
        val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        prefs.edit()
            .putString(LAST_SEEN, formattedDate)
            .apply()
    }

    fun getLastSeen(context: Context): Boolean {
        val prefs = context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE)
        val value = prefs.getString(LAST_SEEN,"")
        var seen = false
        if(!value.equals("")){
            seen = true
        }
        return seen
    }

    fun setLogIn(context: Context, isLoggedIn: Boolean) {
        val prefs = context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean(LOGGED_IN, isLoggedIn)
            .apply()
    }

    fun getLogIn(context: Context): Boolean {
        val prefs = context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE)
        return prefs.getBoolean(LOGGED_IN,false)
    }

    fun setAuthenticationMode(context: Context, authMode: String) {
        val prefs = context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(AUTHENTICATION_MODE, authMode)
            .apply()
    }

    fun getAuthenticationMode(context: Context): String {
        val prefs = context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE)
        val value = prefs.getString(AUTHENTICATION_MODE,"")
        return value!!
    }

    fun savePaymentMethod(context: Context, method: String) {
        val prefs = context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE)
        prefs.edit().putString(PAYMENT_KEY, method).apply()
    }

    fun getPaymentMethod(context: Context): String {
        val prefs = context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE)
        val saved = prefs.getString(PAYMENT_KEY, null)
        return if (saved == null) {
            prefs.edit().putString(PAYMENT_KEY, DEFAULT_METHOD).apply()
            DEFAULT_METHOD
        } else {
            saved
        }
    }

    fun saveCountry(context: Context, country: String) {
        val prefs = context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE)
        prefs.edit().putString(COUNTRY_KEY, country).apply()
    }

    fun getCountry(context: Context): String {
        val prefs = context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE)
        val saved = prefs.getString(COUNTRY_KEY, null)
        return if (saved == null) {
            prefs.edit().putString(COUNTRY_KEY, DEFAULT_COUNTRY).apply()
            DEFAULT_COUNTRY
        } else {
            saved
        }
    }

    fun saveCurrency(context: Context, currency: String) {
        val prefs = context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE)
        prefs.edit().putString(CURRENCY_KEY, currency).apply()
    }

    fun getCurrency(context: Context): String {
        val prefs = context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE)
        val saved = prefs.getString(CURRENCY_KEY, null)
        return if (saved == null) {
            prefs.edit().putString(CURRENCY_KEY, DEFAULT_CURRENCY).apply()
            DEFAULT_CURRENCY
        } else {
            saved
        }
    }
}