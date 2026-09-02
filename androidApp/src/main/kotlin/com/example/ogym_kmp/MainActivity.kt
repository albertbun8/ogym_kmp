package com.example.ogym_kmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ogym_kmp.core.di.AppDependencies
import kotlin.getValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App(appDependencies)
        }
    }
}

private val appDependencies by lazy {
    AppDependencies(
        baseUrl = "http://192.168.2.26:8080"
    )
}
