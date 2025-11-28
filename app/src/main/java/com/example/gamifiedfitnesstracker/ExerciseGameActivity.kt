package com.example.gamifiedfitnesstracker

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlin.time.Duration.Companion.minutes

class ExerciseGameActivity : AppCompatActivity()  {
    private lateinit var game : ExerciseLogger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_exercise_logger)

        // Initialize Game
        val initDuration = 0.minutes
        game = ExerciseLogger(initDuration)

        // Assumption: There will be a Button / Input that allows you to select your exercise.
        // Current Implementation: User inputs which exercise, and autofill takes care of the rest

        // findViewById(R.id.userInput).text == "selected exercise"
        var selected_exercise = ""
        if (true) {
            setBackgroundExercise(selected_exercise)
        }

}
    fun setBackgroundExercise(selected_exercise: String) {
        var background =
            when(selected_exercise) {
            "Running" -> R.drawable.footsteps
            "Squat" -> R.drawable.squat_2
            "Bench Press" -> R.drawable.bench_press
            "Dumbell" -> R.drawable.dumbell
            else -> R.drawable.dumbell // Change; Needed for testing
        }

        val imageView = findViewById<ImageView>(R.id.backgroundExerciseImage)
        imageView.setImageResource(background)
    }
}

