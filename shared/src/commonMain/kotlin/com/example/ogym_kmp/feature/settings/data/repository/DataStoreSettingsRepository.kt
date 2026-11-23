package com.example.ogym_kmp.feature.settings.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.ogym_kmp.core.common.model.WeightUnit
import com.example.ogym_kmp.feature.settings.domain.model.ThemeMode
import com.example.ogym_kmp.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreSettingsRepository(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {
    private companion object {
        val THEME_MODE_KEY: Preferences.Key<String> = stringPreferencesKey("theme_mode")
        val WEIGHT_UNIT_KEY: Preferences.Key<String> = stringPreferencesKey("weight_unit")
    }

    override val themeMode: Flow<ThemeMode> = dataStore.data.map { preferences ->
        val name = preferences[THEME_MODE_KEY]
        ThemeMode.entries.find {
            it.name == name
        } ?: ThemeMode.SYSTEM
    }

    override val weightUnit: Flow<WeightUnit> = dataStore.data.map { preferences ->
        val name = preferences[WEIGHT_UNIT_KEY]
        WeightUnit.entries.find {
            it.name == name
        } ?: WeightUnit.LB
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.name
        }
    }

    override suspend fun setWeightUnit(weightUnit: WeightUnit) {
        dataStore.edit { preferences ->
            preferences[WEIGHT_UNIT_KEY] = weightUnit.name
        }
    }

}