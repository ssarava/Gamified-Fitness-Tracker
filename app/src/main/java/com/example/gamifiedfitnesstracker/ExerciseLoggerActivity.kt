package com.example.gamifiedfitnesstracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.util.Locale

class ExerciseLoggerActivity : AppCompatActivity() {

    private lateinit var workoutName: String
    private lateinit var workoutUnit: String
    private lateinit var currentRepsTV: TextView
    private lateinit var personalBestTV: TextView
    private lateinit var backButton: ImageButton
    private lateinit var game: ExerciseLogger
    private lateinit var ad: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_logger)

        // NEW: Get values passed from SelectWorkoutActivity
        workoutName = intent.getStringExtra(Utilities.WORKOUT_NAME)!!
        workoutUnit = intent.getStringExtra(Utilities.UNIT)!!

        // Pull Views
        val timerText = findViewById<TextView>(R.id.timerText)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val increaseRepsButton = findViewById<Button>(R.id.increaseRepsButton)
        val leaderboardButton = findViewById<ImageButton>(R.id.leaderboardButton)
        backButton = findViewById(R.id.logger_back_button)
        currentRepsTV = findViewById(R.id.currentScoreText)
        personalBestTV = findViewById(R.id.personalBestText)

        // Initialize Game
        val workoutBest = intent.getStringExtra(Utilities.WORKOUT_BEST)!!.toInt()
        game = ExerciseLogger(1, workoutBest, workoutName) // Testing, Fill in with passed input
        currentRepsTV.text = resources.getString(R.string.empty_score_value, game.getCurrentReps())
        personalBestTV.text = resources.getString(R.string.empty_score_value, workoutBest)

        backButton.setOnClickListener { goToMainMenu() }

        // NEW: Populate exercise name + background dynamically
        val exerciseNameView = findViewById<TextView>(R.id.exerciseName)

        if (!Workout.valueOf(workoutName).isUnimplemented()) {
            val workout = Workout.valueOf(workoutName)
            // It's a built-in exercise
            game.setCurrentWorkout(workout)
            setBackgroundExercise(workout)
        } else {
            // Custom workout entry
            Log.w("main", "logging custom/default exercise $workoutName")
            exerciseNameView.text = workoutName
            setBackgroundExercise(Workout.DEFAULT)
        }

        // findViewById(R.id.userInput).text == "selected exercise"
        workoutUnit = workoutUnit.lowercase().replaceFirstChar { it.titlecase() }
        increaseRepsButton.text = resources.getString(R.string.increase_reps_btn_test, workoutUnit)
        increaseRepsButton.setOnClickListener { updateViews() }
        leaderboardButton.setOnClickListener { goToLeaderboard() }

        // Set TimerView and Progress Bar Update functions.
        val timer = game.getCountDownTimer()
        timer.setOnTickListener = { millisLeft ->
            val totalSeconds = (millisLeft / 1000).toInt()
            val minutes = totalSeconds / 60
            timerText.text = String.format(Locale.US, "%02d:%02d", minutes, totalSeconds % 60)
            progressBar.progress = ((60000 - millisLeft) * 100 / 60000).toInt()
        }
        timer.setOnFinishListener = {
            timerText.text = resources.getString(R.string.workout_finished)
            increaseRepsButton.isEnabled = false
            increaseRepsButton.alpha = 0.5f
            progressBar.progress = 100
            goToLeaderboard()
        }


    }

    // Change to when view appears
    override fun onResume() {
        super.onResume()

        // Start Workout
        game.getCountDownTimer().start()
    }

    fun updateViews() {
        // Update Game State
        game.updateReps()
        currentRepsTV.text = game.getCurrentReps().toString()
        personalBestTV.text = game.getPersonalBest().toString()
    }

    fun setBackgroundExercise(selectedExercise: Workout) {
        val background =
            when (selectedExercise) {
                Workout.BENCH_PRESS -> R.drawable.bench_press_2
                Workout.CURL -> R.drawable.dumbell
                Workout.PUSH_UP -> R.drawable.push_up
                Workout.RUN -> R.drawable.footsteps
                Workout.SQUAT -> R.drawable.squat_2
                else -> R.drawable.generic_exercise // Change; Needed for testing
            }

        val imageView = findViewById<ImageView>(R.id.backgroundExerciseImage)
        val exerciseNameView = findViewById<TextView>(R.id.exerciseName)
        imageView.setImageResource(background)
        exerciseNameView.text = selectedExercise.displayName
    }

    fun goToMainMenu() {
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
        finish() // Remove from stack
    }

    fun goToLeaderboard() {
        backButton.isEnabled = false
        game.saveToFirebase(this)
        val intent = Intent(this, LeaderboardActivity::class.java)
        intent.putExtra(Utilities.WORKOUT_NAME, workoutName)
        intent.putExtra(Utilities.UNIT, workoutUnit)

        // set up advertisement
        val builder = AdRequest.Builder()
        builder.addKeyword("exercise")
        val request = builder.build()

        val adUnitId = "ca-app-pub-3940256099942544/1033173712"
        val adLoadHandler = AdLoadHandler(intent)
        InterstitialAd.load(this, adUnitId, request, adLoadHandler)

//        startActivity(intent)
//        finish() // Remove from stack
    }

    inner class AdLoadHandler() : InterstitialAdLoadCallback() {

        private lateinit var intent: Intent

        constructor(intentIn: Intent) : this() {
            intent = intentIn
        }

        override fun onAdLoaded(p0: InterstitialAd) {
            super.onAdLoaded(p0)
            ad = p0
            ad.show(this@ExerciseLoggerActivity)
            ad.fullScreenContentCallback = AdManagement(intent)     // manage the ad
        }
    }

    inner class AdManagement() : FullScreenContentCallback() {
        lateinit var intent: Intent

        constructor(intentIn: Intent) : this() {
            intent = intentIn
        }

        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()
            startActivity(intent)
            this@ExerciseLoggerActivity.finish()
        }
    }
}

