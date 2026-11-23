package com.example.ogym_kmp.feature.settings

import com.example.ogym_kmp.core.common.model.WeightUnit
import com.example.ogym_kmp.feature.settings.domain.model.ThemeMode
import com.example.ogym_kmp.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSettingsRepository (
    initialThemeMode: ThemeMode = ThemeMode.SYSTEM,
    initialWeightUnit: WeightUnit = WeightUnit.KG
) : SettingsRepository {
    private val _themeMode = MutableStateFlow(initialThemeMode)
    override val themeMode: Flow<ThemeMode> = _themeMode

    private val _weightUnit = MutableStateFlow(initialWeightUnit)
    override val weightUnit: Flow<WeightUnit> = _weightUnit

    override suspend fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    override suspend fun setWeightUnit(weightUnit: WeightUnit) {
        _weightUnit.value = weightUnit
    }
}