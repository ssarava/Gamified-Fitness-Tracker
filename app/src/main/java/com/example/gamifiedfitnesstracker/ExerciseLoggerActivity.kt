package com.example.gamifiedfitnesstracker

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.time.Duration.Companion.minutes
import android.widget.ImageButton

class ExerciseLoggerActivity : AppCompatActivity()  {

    companion object {
        const val INCREASE_PREFIX = "Increase"
    }
    private lateinit var game : ExerciseLogger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_logger)

        // NEW: Get values passed from SelectWorkoutActivity
        val workoutName = intent.getStringExtra("workout_name") ?: Workout.DEFAULT.displayName
        val workoutEnumName = intent.getStringExtra("workout_enum")
        var measurementType = intent.getStringExtra("measurement_type") ?: "reps"


        // Initialize Game
        val initDuration = 0.minutes
        game = ExerciseLogger(1) // Testing, Fill in with passed input

        // Pull Views
        val timerText = findViewById<TextView>(R.id.timerText)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val increaseRepsButton = findViewById<Button>(R.id.increaseRepsButton)
        val leaderboardButton = findViewById<ImageButton>(R.id.leaderboardButton)

        // NEW: Populate exercise name + background dynamically
        val exerciseNameView = findViewById<TextView>(R.id.exerciseName)

        if (workoutEnumName != null && workoutEnumName != "CUSTOM") {
            // It's a built-in exercise
            val workoutEnum = Workout.valueOf(workoutEnumName)
            game.setCurrentWorkout(workoutEnum)
            setBackgroundExercise(workoutEnum)
        } else {
            // Custom workout entry
            exerciseNameView.text = workoutName
            setBackgroundExercise(Workout.DEFAULT)
        }

        // findViewById(R.id.userInput).text == "selected exercise"
        // Need to pass in an enum
        measurementType = measurementType.lowercase().replaceFirstChar { it.titlecase() }
        increaseRepsButton.text = INCREASE_PREFIX + " " + measurementType

//        if (measurementType.lowercase() == "miles") {
//            increaseRepsButton.text = "Increase Miles"
//        } else {
//            increaseRepsButton.text = "Increase Reps"
//        }
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

    }


    // Change to when view appears
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
        // existing placeholder
    }
}

