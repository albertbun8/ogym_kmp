package com.example.ogym_kmp.feature.settings.domain.repository

import com.example.ogym_kmp.core.common.model.WeightUnit
import com.example.ogym_kmp.feature.settings.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val themeMode: Flow<ThemeMode>
    val weightUnit: Flow<WeightUnit>

    suspend fun setThemeMode(mode: ThemeMode)

    suspend fun setWeightUnit(weightUnit: WeightUnit)
}