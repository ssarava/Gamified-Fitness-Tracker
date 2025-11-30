package com.example.testerapplication

import android.content.Context
import android.content.SharedPreferences
import android.os.CountDownTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes


class ExerciseLogger {
    private var userScore = 0
    private var currentReps : Int = 0 // Represents #/amount of exercise done. Potential rename for clarity.
    private var caloriesBurned : Double = 0.0
    private var personalBest = 0
    private val activity: ExerciseLoggerActivity

    private var currentGame: Workout? = null
    private var countDownTimer: ExerciseTimer

    // User inputs the duration of exercise.
    constructor(activity: ExerciseLoggerActivity, exerciseInMinutes: Int) {
        this.activity = activity
        this.countDownTimer = ExerciseTimer(activity, exerciseInMinutes)
    }

    // Returns true if we have a new personal best.
    fun updateReps() : Boolean {
        this.currentReps += 1

        if(this.currentReps > personalBest) {
            this.personalBest = this.currentReps

            val sharedPreferences: SharedPreferences? = activity.getSharedPreferences(currentGame?.displayName, Context.MODE_PRIVATE)
            val prefenceEditor = sharedPreferences?.edit()
            prefenceEditor?.putInt(currentGame?.displayName, personalBest)?.apply()
            return true
        }
        return false
    }

    fun resetGame() {
        this.userScore = 0
        this.currentReps = 0
        this.caloriesBurned = 0.0
        this.personalBest = 0
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

    fun getActivity(): ExerciseLoggerActivity {
        return activity
    }

    fun getCurrentGame(): Workout {
        return currentGame!!
    }

    fun setCurrentGame(game: Workout) {
        currentGame = game
    }

    fun getCountDownTimer() : ExerciseTimer {
        return this.countDownTimer
    }
}
