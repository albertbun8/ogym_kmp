package com.example.ogym_kmp.feature.settings.di

import com.example.ogym_kmp.core.preferences.WeightUnitPreferences
import com.example.ogym_kmp.feature.settings.data.repository.DataStoreSettingsRepository
import com.example.ogym_kmp.feature.settings.domain.repository.SettingsRepository
import com.example.ogym_kmp.feature.settings.presentation.SettingsViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    singleOf(::DataStoreSettingsRepository) {
        bind<SettingsRepository>()
        bind<WeightUnitPreferences>()
    }
    viewModel { SettingsViewModel(get()) }
}