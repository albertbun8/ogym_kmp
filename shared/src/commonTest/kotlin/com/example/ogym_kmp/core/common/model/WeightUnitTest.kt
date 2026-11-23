package com.example.ogym_kmp.core.common.model

import kotlin.test.Test
import kotlin.test.assertEquals

class WeightUnitTest {
    @Test
    fun sameUnit_returnsSameWeight() {
        val result = convertWeight(100.0, WeightUnit.LB, WeightUnit.LB)
        assertEquals(100.0, result, absoluteTolerance = 0.001)
    }

    @Test
    fun poundsToKilograms() {
        val result = convertWeight(100.0, from = WeightUnit.LB, to = WeightUnit.KG)

        assertEquals(45.359237, result, absoluteTolerance = 0.001)
    }

    @Test
    fun kilogramsToPounds() {
        val result = convertWeight(100.0, from = WeightUnit.KG, to = WeightUnit.LB)

        assertEquals(220.462, result, absoluteTolerance = 0.001)
    }

    @Test
    fun roundTrip_returnsOriginalWeight() {
        val kilograms = convertWeight(135.0, WeightUnit.LB, WeightUnit.KG)
        val backToPounds = convertWeight(kilograms, WeightUnit.KG, WeightUnit.LB)

        assertEquals(135.0, backToPounds, absoluteTolerance = 0.001)
    }
}