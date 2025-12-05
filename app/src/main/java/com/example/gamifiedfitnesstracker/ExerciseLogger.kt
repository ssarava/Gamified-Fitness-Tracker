package com.example.gamifiedfitnesstracker


class ExerciseLogger {
    private var userScore = 0
    private var currentReps : Int = 0 // Represents #/amount of exercise done. Potential rename for clarity.
    private var caloriesBurned : Double = 0.0
    private var personalBest = 0

    private var currentWorkout: Workout? = null
    private var countDownTimer: ExerciseTimer

    // User inputs the duration of exercise.
    constructor(exerciseInMinutes: Int) {
        this.countDownTimer = ExerciseTimer(exerciseInMinutes)
    }

    // Returns true if we have a new personal best.
    fun updateReps() : Boolean {
        this.currentReps += 1

        if(this.currentReps > personalBest) {
            this.personalBest = this.currentReps
        }
        return false
    }

    fun resetGame() {
        this.userScore = 0
        this.currentReps = 0
        this.caloriesBurned = 0.0
        this.personalBest = 0
        this.currentWorkout = null
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

    fun getCurrentWorkout(): Workout {
        return currentWorkout!!
    }

    fun setCurrentWorkout(game: Workout) {
        currentWorkout = game
    }

    fun getCountDownTimer() : ExerciseTimer {
        return this.countDownTimer
    }
}
