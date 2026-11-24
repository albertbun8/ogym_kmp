package com.example.ogym_kmp.core.preferences

import com.example.ogym_kmp.core.common.model.WeightUnit
import kotlinx.coroutines.flow.Flow

interface WeightUnitPreferences {
    val weightUnit: Flow<WeightUnit>
}