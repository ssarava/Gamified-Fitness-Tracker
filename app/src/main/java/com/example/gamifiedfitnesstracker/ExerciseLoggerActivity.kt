package com.example.gamifiedfitnesstracker

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.time.Duration.Companion.minutes

class ExerciseLoggerActivity : AppCompatActivity()  {
    private lateinit var game : ExerciseLogger


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_exercise_logger)

        // Initialize Game
        val initDuration = 0.minutes
        game = ExerciseLogger(this, initDuration)


        // Assumption: There will be a Button / Input that allows you to select your exercise.
        // Current Implementation: User inputs which exercise, and autofill takes care of the rest

        // findViewById(R.id.userInput).text == "selected exercise"
        var selected_exercise = ""
        if (true) {
            setBackgroundExercise(selected_exercise)
        }
        val increaseRepsButton = findViewById<Button>(R.id.increaseRepsButton)
        increaseRepsButton.setOnClickListener { updateReps() }
}
    fun updateReps() {
        val currentReps = findViewById<TextView>(R.id.currentScoreText)
        val personalBest = findViewById<TextView>(R.id.personalBestText)

        // Update Game State
        game.updateReps()
        currentReps.text = game.getCurrentReps().toString()
        personalBest.text = game.getPersonalBest().toString()
    }
    fun setBackgroundExercise(selected_exercise: String) {
        var background =
            when(selected_exercise) {
            "Running" -> R.drawable.footsteps
            "Squat" -> R.drawable.squat_2
            "Bench Press" -> R.drawable.ic_dumbbell
            "Dumbell" -> R.drawable.dumbell
            else -> R.drawable.generic_exercise // Change; Needed for testing
        }

        val imageView = findViewById<ImageView>(R.id.backgroundExerciseImage)
        val exerciseNameView = findViewById<TextView>(R.id.exerciseName)
        imageView.setImageResource(background)
        exerciseNameView.text = selected_exercise
    }

    fun saveToFirebase() {

    }
}

