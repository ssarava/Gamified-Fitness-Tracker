package com.example.gamifiedfitnesstracker

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class ExerciseLogger {
    private var exerciseDuration : Duration
    private var userScore = 0
    private var reps : Int = 0 // Represents #/amount of exercise done. Potential rename for clarity.
    private var caloriesBurned : Double = 0.0
    private var highScore = 0

    // User inputs the duration of exercise.
    constructor(exerciseDuration: Duration) {
        this.exerciseDuration = exerciseDuration
    }

    fun resetGame() {
        this.exerciseDuration = 0.minutes
        this.userScore = 0
        this.reps = 0
        this.caloriesBurned = 0.0
        this.highScore = 0
    }







}