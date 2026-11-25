package com.example.ogym_kmp.core.preferences

import com.example.ogym_kmp.core.common.model.WeightUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeWeightUnitPreferences(
    initialWeightUnit: WeightUnit = WeightUnit.LB
) : WeightUnitPreferences {
    private val _weightUnit = MutableStateFlow(initialWeightUnit)
    override val weightUnit: Flow<WeightUnit> = _weightUnit
}