package com.example.ogym_kmp

import android.app.Application
import com.example.ogym_kmp.core.database.DatabaseDriverFactory
import com.example.ogym_kmp.core.di.AppDependencies

class OGymApplication : Application() {
    val appDependencies by lazy {
        AppDependencies(
            baseUrl = "http://192.168.2.26:8080",
            databaseDriverFactory = DatabaseDriverFactory(applicationContext),
        )
    }

}