package com.example.ogym_kmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ogym_kmp.core.di.AppDependencies
import com.example.ogym_kmp.core.database.DatabaseDriverFactory
import kotlin.getValue

class MainActivity : ComponentActivity() {
    private val appDependencies by lazy {
        AppDependencies(
            baseUrl = "http://192.168.2.26:8080",
            databaseDriverFactory = DatabaseDriverFactory(applicationContext),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            ),
        )
        super.onCreate(savedInstanceState)

        setContent {
            App(appDependencies)
        }
    }
}
