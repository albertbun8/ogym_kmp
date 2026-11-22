package com.example.ogym_kmp.feature.settings.di

import com.example.ogym_kmp.feature.settings.data.repository.DataStoreSettingsRepository
import com.example.ogym_kmp.feature.settings.domain.repository.SettingsRepository
import com.example.ogym_kmp.feature.settings.presentation.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    single<SettingsRepository> { DataStoreSettingsRepository(get()) }

    viewModel { SettingsViewModel(get()) }
}