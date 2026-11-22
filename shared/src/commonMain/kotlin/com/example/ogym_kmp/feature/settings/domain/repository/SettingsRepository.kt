package com.example.ogym_kmp.feature.settings.domain.repository

import com.example.ogym_kmp.feature.settings.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val themeMode: Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)
}