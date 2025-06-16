package com.example.buynest.utils.sharedPreferences

import android.content.Context

interface SharedPreferences {
    fun setLastSeen(context: Context)
    fun getLastSeen(context: Context):Boolean
}