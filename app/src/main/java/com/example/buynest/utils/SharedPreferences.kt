package com.example.buynest.utils

import android.content.Context

interface SharedPreferences {
    fun setLastSeen(context: Context)
    fun getLastSeen(context: Context):Boolean
}