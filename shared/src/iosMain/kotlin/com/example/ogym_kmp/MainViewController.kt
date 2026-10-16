package com.example.ogym_kmp

import androidx.compose.ui.window.ComposeUIViewController
import com.example.ogym_kmp.core.di.AppDependencies
import com.example.ogym_kmp.core.database.DatabaseDriverFactory

private val appDependencies by lazy {
    AppDependencies(
        baseUrl = "http://127.0.0.1:8080",
        databaseDriverFactory = DatabaseDriverFactory(),
    )
}

fun MainViewController() = ComposeUIViewController {
    App()
}
