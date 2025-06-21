package com.example.buynest.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SecureSharedPrefHelper {
    const val PREF_FILE_NAME = "secure_prefs"
    private var sharedPreferences: EncryptedSharedPreferences? = null


    fun init(context: Context) {
        val masterKeyAlias = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        sharedPreferences = EncryptedSharedPreferences.create(
            context,
            PREF_FILE_NAME,
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as EncryptedSharedPreferences?
    }

    fun putString(key: String, value: String) {
        sharedPreferences?.edit()?.putString(key, value)?.apply()
            ?: throw IllegalStateException("SecureSharedPrefHelper is not initialized")
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        return sharedPreferences?.getString(key, defaultValue)
            ?: throw IllegalStateException("SecureSharedPrefHelper is not initialized")
    }

    fun remove(key: String) {
        sharedPreferences?.edit()?.remove(key)?.apply()
            ?: throw IllegalStateException("SecureSharedPrefHelper is not initialized")
    }
}
