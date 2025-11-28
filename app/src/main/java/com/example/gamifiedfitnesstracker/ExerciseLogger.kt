package com.example.gamifiedfitnesstracker

import kotlin.time.Duration

class ExerciseRecorder {
    private val exerciseDuration : Duration
    private val userScore = 0
    private val reps : Int = 0 // Represents #/amount of exercise done. Potential rename for clarity.
    private val caloriesBurned : Double = 0.0
    private val highScore = 0

    // User inputs the duration of exercise.
    constructor(exerciseDuration: Duration) {
        this.exerciseDuration = exerciseDuration
    }



}