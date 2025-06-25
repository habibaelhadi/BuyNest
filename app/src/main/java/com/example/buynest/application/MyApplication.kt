package com.example.buynest.application

import android.app.Application
import com.example.buynest.di.diModule
import com.example.buynest.utils.SecureSharedPrefHelper
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        SecureSharedPrefHelper.init(this)
        startKoin {
            androidContext(this@MyApplication)
            modules(diModule)
        }
    }
}