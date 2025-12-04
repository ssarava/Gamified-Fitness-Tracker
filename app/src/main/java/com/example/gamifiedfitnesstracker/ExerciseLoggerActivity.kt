package com.example.gamifiedfitnesstracker

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.time.Duration.Companion.minutes

class ExerciseLoggerActivity : AppCompatActivity() {
    private lateinit var game: ExerciseLogger
    private lateinit var incrementRepsBtn: Button
    private lateinit var currentRepsTV: TextView
    private lateinit var personalBestTV: TextView
    private lateinit var backgroundExerciseImage: ImageView
    private lateinit var exerciseNameTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_logger)

        initializeViews()

        // Initialize Game
        game = ExerciseLogger(0.minutes)


        // Assumption: There will be a Button / Input that allows you to select your exercise.
        // Current Implementation: User inputs which exercise, and autofill takes care of the rest

        // findViewById(R.id.userInput).text == "selected exercise"
        val selectedExercise = ""
        if (true) {
            setBackgroundExercise(selectedExercise)
        }
        incrementRepsBtn.setOnClickListener { updateReps() }
    }

    fun initializeViews() {
        incrementRepsBtn = findViewById(R.id.increaseRepsButton)
        currentRepsTV = findViewById(R.id.currentScoreText)
        personalBestTV = findViewById(R.id.personalBestText)
        backgroundExerciseImage = findViewById(R.id.backgroundExerciseImage)
        exerciseNameTV = findViewById(R.id.exerciseName)
    }

    fun updateReps() {
        // Update Game State
        game.updateReps(this)
        currentRepsTV.text = game.getCurrentReps().toString()
        personalBestTV.text = game.getPersonalBest().toString()
    }

    fun setBackgroundExercise(selectedExercise: String) {
        val background =
            when (selectedExercise) {
                "Running" -> R.drawable.footsteps
                "Squat" -> R.drawable.squat_2
                "Bench Press" -> R.drawable.ic_dumbbell
                "Dumbbell" -> R.drawable.dumbell
                else -> R.drawable.generic_exercise // Change; Needed for testing
            }

        backgroundExerciseImage.setImageResource(background)
        exerciseNameTV.text = selectedExercise
    }

    fun saveToFirebase() {

    }
}

