package com.example.ogym_kmp.core.di

import com.example.ogym_kmp.core.database.DatabaseDriverFactory
import com.example.ogym_kmp.core.database.OGymDatabase
import com.example.ogym_kmp.core.network.createHttpClient
import org.koin.dsl.module

fun coreModule(
    baseUrl: String,
    databaseDriverFactory: DatabaseDriverFactory
) = module {
    single { createHttpClient(baseUrl) }
    single { OGymDatabase(databaseDriverFactory.createDriver()) }
}