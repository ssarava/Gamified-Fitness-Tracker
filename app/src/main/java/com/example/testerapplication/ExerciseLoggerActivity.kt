package com.example.testerapplication

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.time.Duration.Companion.minutes

class ExerciseLoggerActivity : AppCompatActivity()  {

    private lateinit var game : ExerciseLogger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_logger)

        // NEW: Get values passed from SelectWorkoutActivity
        val workoutName = intent.getStringExtra("workout_name") ?: "Exercise"
        val workoutEnumName = intent.getStringExtra("workout_enum")
        val measurementType = intent.getStringExtra("measurement_type") ?: "reps"


        // Initialize Game
        val initDuration = 0.minutes
        game = ExerciseLogger(this, 1) // Testing

        // Pull Views
        val timerText = findViewById<TextView>(R.id.timerText)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val increaseRepsButton = findViewById<Button>(R.id.increaseRepsButton)

        // NEW: Populate exercise name + background dynamically
        val exerciseNameView = findViewById<TextView>(R.id.exerciseName)

        if (workoutEnumName != null && workoutEnumName != "CUSTOM") {
            // It's a built-in exercise
            val workoutEnum = Workout.valueOf(workoutEnumName)
            game.setCurrentGame(workoutEnum)
            setBackgroundExercise(workoutEnum)
        } else {
            // Custom workout entry
            exerciseNameView.text = workoutName
        }

        // NEW: Change button label depending on measurement
        if (measurementType.lowercase() == "miles") {
            increaseRepsButton.text = "Increase Miles"
        } else {
            increaseRepsButton.text = "Increase Reps"
        }


        increaseRepsButton.setOnClickListener { updateReps() }

        // Set TimerView and Progress Bar Updates
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

        // Start Workout
        timer.start()
    }

    fun updateReps() {
        val currentReps = findViewById<TextView>(R.id.currentScoreText)
        val personalBest = findViewById<TextView>(R.id.personalBestText)

        // Update Game State
        game.updateReps()
        currentReps.text = game.getCurrentReps().toString()
        personalBest.text = game.getPersonalBest().toString()
    }

    fun setBackgroundExercise(selected_exercise: Workout) {
        val background = when(selected_exercise) {
            Workout.RUN -> R.drawable.footsteps
            Workout.SQUAT -> R.drawable.squat_2
            Workout.BENCH_PRESS -> R.drawable.bench_press_2
            Workout.CURL -> R.drawable.dumbell
            else -> R.drawable.generic_exercise
        }

        val imageView = findViewById<ImageView>(R.id.backgroundExerciseImage)
        val exerciseNameView = findViewById<TextView>(R.id.exerciseName)

        imageView.setImageResource(background)
        exerciseNameView.text = selected_exercise.displayName
    }

    fun saveToFirebase() {
        // existing placeholder
    }
}
