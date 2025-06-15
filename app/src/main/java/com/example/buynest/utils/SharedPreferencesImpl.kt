package com.example.buynest.utils

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object SharedPreferencesImpl:
    SharedPreferences {

    private const val APP_PREFS = "app_prefs"
    private const val LAST_SEEN = "skip_splash"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setLastSeen(context: Context) {
        val prefs = context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE)
        val currentDate = LocalDate.now()
        val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        prefs.edit()
            .putString(LAST_SEEN, formattedDate)
            .apply()
    }

    override fun getLastSeen(context: Context): Boolean {
        val prefs = context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE)
        val value = prefs.getString(LAST_SEEN,"")
        var seen = false
        if(!value.equals("")){
            seen = true
        }
        return seen
    }
}