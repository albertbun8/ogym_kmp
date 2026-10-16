package com.example.ogym_kmp.core.di

import com.example.ogym_kmp.core.database.DatabaseDriverFactory

fun initKoinIos() {
    initKoin(
        baseUrl = "http://127.0.0.1:8080",
        databaseDriverFactory = DatabaseDriverFactory(),
    )
}