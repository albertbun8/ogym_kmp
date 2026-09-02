package com.example.ogym_kmp.feature.exercise.domain.model

import kotlin.uuid.Uuid

data class Exercise(
    val id: Uuid,
    val name: String,
    val muscleGroups: List<MuscleGroup>,
    val ownerId: Uuid? = null
){
    init {
        require(name.isNotBlank()){
            "Exercise name must not be blank."
        }
        require(muscleGroups.isNotEmpty()){
            "Exercise must have at least one muscle group."
        }
    }
}