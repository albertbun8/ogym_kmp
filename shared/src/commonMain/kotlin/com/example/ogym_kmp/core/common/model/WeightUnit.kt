package com.example.ogym_kmp.core.common.model

enum class WeightUnit {
    LB,
    KG,
}

private const val KG_PER_LB = 0.45359237

fun convertWeight(weight: Double, from: WeightUnit, to: WeightUnit): Double =
    when {
        from == to -> weight
        from == WeightUnit.LB -> weight * KG_PER_LB
        else -> weight / KG_PER_LB
    }