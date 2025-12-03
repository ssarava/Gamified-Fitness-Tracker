package com.example.testerapplication

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.time.Duration.Companion.minutes

class ExerciseLoggerActivity : AppCompatActivity()  {

    companion object {
        const val SHARED_PREFERENCE_IDENTIFIER = "Most Recent Exercise"
    }
    private lateinit var game : ExerciseLogger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_logger)

        // Initialize Game
        val initDuration = 0.minutes
        game = ExerciseLogger(1) // Testing, Fill in with passed input

        // Pull Views
        val timerText = findViewById<TextView>(R.id.timerText)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val increaseRepsButton = findViewById<Button>(R.id.increaseRepsButton)
        val leaderboardButton = findViewById<ImageButton>(R.id.leaderboardButton)

        // Assumption: There will be a Button / Input that allows you to select your exercise.
        // Current Implementation: User inputs which exercise, and autofill takes care of the rest

        // findViewById(R.id.userInput).text == "selected exercise"
        // Need to pass in an enum
        var selectedExercise: Workout
        if (true) {
//            setBackgroundExercise(selectedExercise)
            setBackgroundExercise(Workout.RUN) // Testing
        }
        increaseRepsButton.setOnClickListener { updateReps() }
//        leaderboardButton.setOnClickListener { setContentView(R.layout.leaderboard) } Use when leaderboard implemented
        


        // Set TimerView and Progress Bar Update functions.
        val timer = game.getCountDownTimer()
        timer.setOnTickListener = { millisLeft ->
            val secondsLeft = (millisLeft / 1000).toInt()
            timerText.text = String.format("%02d:%02d", secondsLeft / 60, secondsLeft % 60)
            progressBar.progress = ((60000 - millisLeft) * 100 / 60000).toInt()
        }
        timer.setOnFinishListener = {
            timerText.text = "Workout Finished"
            increaseRepsButton.isEnabled = false
            increaseRepsButton.alpha = 0.5f
            progressBar.progress = 100
        }

        game.setCurrentWorkout(Workout.RUN) // Testing pevents null pointer
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(SHARED_PREFERENCE_IDENTIFIER, Context.MODE_PRIVATE)
        val prefenceEditor = sharedPreferences.edit()
        prefenceEditor.putString(SHARED_PREFERENCE_IDENTIFIER, game.getCurrentWorkout().displayName).apply()
    }

    override fun onResume() {
        super.onResume()

        // Start Workout
        game.getCountDownTimer().start()
    }
    fun updateReps() {
        val currentReps = findViewById<TextView>(R.id.currentScoreText)
        val personalBest = findViewById<TextView>(R.id.personalBestText)

        // Update Game State
        game.updateReps()
        currentReps.text = game.getCurrentReps().toString()
        personalBest.text = game.getPersonalBest().toString()
    }
    fun setBackgroundExercise(selectedExercise: Workout) {
        var background =
            when(selectedExercise) {
            Workout.RUN -> R.drawable.footsteps
            Workout.SQUAT -> R.drawable.squat_2
            Workout.BENCH_PRESS -> R.drawable.bench_press_2
            Workout.CURL -> R.drawable.dumbell
            else -> R.drawable.generic_exercise // Change; Needed for testing
        }

        val imageView = findViewById<ImageView>(R.id.backgroundExerciseImage)
        val exerciseNameView = findViewById<TextView>(R.id.exerciseName)
        imageView.setImageResource(background)
        exerciseNameView.text = selectedExercise.displayName
    }

    fun saveToFirebase() {

    }
}

