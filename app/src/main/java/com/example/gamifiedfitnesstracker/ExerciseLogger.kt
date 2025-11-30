package com.example.gamifiedfitnesstracker

import android.content.Context
import androidx.core.content.edit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes


class ExerciseLogger {
    private var exerciseDuration: Duration
    private var userScore = 0
    private var currentReps = 0 // amount of exercise done. Potential rename for clarity.
    private var caloriesBurned = 0.0
    private var personalBest = 0

    private var currentGame: Workout? = null

    // User inputs the duration of exercise.
    constructor(exerciseDuration: Duration) {
        this.exerciseDuration = exerciseDuration
    }

    // Returns true if we have a new personal best.
    fun updateReps(context: Context): Boolean {
        this.userScore += 1

        if (this.currentReps > personalBest) {
            this.personalBest = this.currentReps

            val sp = context.getSharedPreferences(currentGame!!.displayName, Context.MODE_PRIVATE)
            sp.edit(commit = true) {
                putInt(currentGame!!.displayName, personalBest)
            }
            return true
        }
        return false
    }

    fun resetGame() {
        this.exerciseDuration = 0.minutes
        this.userScore = 0
        this.currentReps = 0
        this.caloriesBurned = 0.0
        this.personalBest = 0
    }

    fun getExerciseDuration(): Duration {
        return exerciseDuration
    }

    fun setExerciseDuration(duration: Duration) {
        exerciseDuration = duration
    }

    fun getUserScore(): Int {
        return userScore
    }

    fun setUserScore(score: Int) {
        userScore = score
    }

    fun getCurrentReps(): Int {
        return currentReps
    }

    fun setCurrentReps(reps: Int) {
        currentReps = reps
    }

    fun getCaloriesBurned(): Double {
        return caloriesBurned
    }

    fun setCaloriesBurned(calories: Double) {
        caloriesBurned = calories
    }

    fun getPersonalBest(): Int {
        return personalBest
    }

    fun setPersonalBest(best: Int) {
        personalBest = best
    }


    fun getCurrentGame(): Workout {
        return currentGame!!
    }

    fun setCurrentGame(game: Workout) {
        currentGame = game
    }
}
