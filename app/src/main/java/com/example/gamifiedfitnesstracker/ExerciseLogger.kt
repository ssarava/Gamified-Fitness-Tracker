package com.example.gamifiedfitnesstracker

import android.content.Context
import android.content.Context.MODE_PRIVATE
import kotlin.math.max

class ExerciseLogger {
    private var userScore = 0
    private var repsCompleted: Int = 0
    private var caloriesBurned: Double = 0.0
    private var personalBest = 0
    private var currentWorkout: String
    private var countDownTimer: ExerciseTimer

    // User inputs the duration of exercise.
    constructor(exerciseInMinutes: Int, personalBestIn: Int, workOutNameIn: String) {
        countDownTimer = ExerciseTimer(exerciseInMinutes)
        currentWorkout = workOutNameIn
        personalBest = personalBestIn
    }

    fun updateReps() {
        repsCompleted += 1
        personalBest = max(repsCompleted, personalBest)
    }

    fun saveToFirebase(context: Context) {
        val sp = context.getSharedPreferences("${context.packageName}_preferences", MODE_PRIVATE)
        val username = sp.getString(Utilities.PREFERENCE_USERNAME, "")!!
        Utilities.USERS.child(username).child("Personal Bests")
            .child(Workout.valueOf(currentWorkout).displayName)
            .setValue(personalBest)
    }

    fun resetGame() {
        userScore = 0
        repsCompleted = 0
        caloriesBurned = 0.0
        personalBest = 0
        currentWorkout = ""
    }

    fun getUserScore() = userScore

    fun setUserScore(score: Int) {
        userScore = score
    }

    fun getCurrentReps() = repsCompleted

    fun setCurrentReps(reps: Int) {
        repsCompleted = reps
    }

    fun getPersonalBest() = personalBest

    fun getCurrentWorkout() = Workout.valueOf(currentWorkout)

    fun setCurrentWorkout(game: Workout) {
        currentWorkout = game.name
    }

    fun getCountDownTimer() = countDownTimer
}
