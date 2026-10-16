package com.example.ogym_kmp

import android.app.Application
import com.example.ogym_kmp.core.database.DatabaseDriverFactory
import com.example.ogym_kmp.core.di.initKoin
import org.koin.android.ext.koin.androidLogger

class OGymApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(
            baseUrl = "http://192.168.2.26:8080",
            databaseDriverFactory = DatabaseDriverFactory(this),
        ) {
            androidLogger()
        }
    }
}