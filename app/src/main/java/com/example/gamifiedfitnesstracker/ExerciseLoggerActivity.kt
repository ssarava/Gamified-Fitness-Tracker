package com.example.gamifiedfitnesstracker

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton
import android.widget.Toast
import java.util.Locale

class ExerciseLoggerActivity : AppCompatActivity() {

    private lateinit var workoutName: String
    private lateinit var workoutUnit: String
    private lateinit var currentRepsTV: TextView
    private lateinit var personalBestTV: TextView
    private lateinit var backButton: ImageButton
    private lateinit var game: ExerciseLogger
    private lateinit var emailManager: EmailNotificationManager
    private var currentUsername: String = ""
    private var currentUserEmail: String = ""
    private var previousPersonalBest: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_logger)

        // Initialize email manager
        emailManager = EmailNotificationManager(this)

        // Get current user info
        val sp = getSharedPreferences("${packageName}_preferences", MODE_PRIVATE)
        currentUsername = sp.getString(Utilities.PREFERENCE_USERNAME, "") ?: ""
        currentUserEmail = sp.getString(Utilities.PREFERENCE_EMAIL, "") ?: ""

        // Get values passed from SelectWorkoutActivity
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
        previousPersonalBest = workoutBest  // Store initial personal best
        game = ExerciseLogger(1, workoutBest, workoutName)
        currentRepsTV.text = resources.getString(R.string.empty_score_value, game.getCurrentReps())
        personalBestTV.text = resources.getString(R.string.empty_score_value, workoutBest)

        backButton.setOnClickListener { finish() }

        val workout = Workout.valueOf(workoutName)
        game.setCurrentWorkout(workout)
        setBackgroundExercise(workout)

        workoutUnit = workoutUnit.lowercase().replaceFirstChar { it.titlecase() }
        increaseRepsButton.text = resources.getString(R.string.increase_reps_btn_test, workoutUnit)
        increaseRepsButton.setOnClickListener { updateViews() }

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
            game.saveToFirebase(this)
            timer.cancel()

            // Check leaderboard before going to leaderboard screen
            checkLeaderboardAndOfferEmail {
                finish()
                goToLeaderboard()}

        }
        leaderboardButton.setOnClickListener {
            timer.cancel()
            leaderboardButton.isEnabled = false
            game.saveToFirebase(this)

            // Check leaderboard before going to leaderboard screen
            checkLeaderboardAndOfferEmail{
                finish()
                goToLeaderboard()
            }

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

    /**
     * Check if user has taken #1 spot and offer email options
     */
    private fun checkLeaderboardAndOfferEmail(
        onFinished: () -> Unit
    ) {
        val currentPersonalBest = game.getPersonalBest()

        // Only check if personal best improved
        if (currentPersonalBest <= previousPersonalBest) {
            return
        }

        emailManager.checkAndNotifyLeaderboardChange(
            game.getCurrentWorkout(),
            currentUsername,
            currentPersonalBest
        ) { result ->
            when (result) {

                is LeaderboardChangeResult.NewLeader -> {
                    showEmailOptionsDialog(result, onFinished)
                }

                LeaderboardChangeResult.FirstEntry,
                LeaderboardChangeResult.NoChange -> {
                    onFinished()
                }

                is LeaderboardChangeResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                    onFinished()
                }
            }
        }
    }

    /**
     * Show dialog with email options when user becomes #1
     */
    private fun showEmailOptionsDialog(result: LeaderboardChangeResult.NewLeader,
                                       onFinished: () -> Unit) {
        val workoutName = result.workoutType.displayName
        val options = arrayOf(
            "Send congratulations to myself",
            "Send 'bragging rights' email to ${result.formerLeader.username}",
            "Send both emails!",
            "Skip"
        )

        AlertDialog.Builder(this)
            .setTitle("🎉 You're now #1 in $workoutName! Select one the following options:")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> {
                        // Send congratulations to self
                        emailManager.sendCongratulationsEmail(
                            recipientEmail = currentUserEmail,
                            recipientName = currentUsername,
                            workout = result.workoutType,
                            newScore = result.newLeader.score
                        )
                        onFinished()
                    }
                    1 -> {
                        // Send bragging email to former leader
                        emailManager.sendBraggingRightsEmail(
                            result.formerLeader.email,
                            result.formerLeader.username,
                            currentUsername,
                            result.workoutType,
                            result.newLeader.score,
                            result.formerLeader.score
                        )
                        onFinished()
                    }
                    2 -> {
                        // Send both emails
                        emailManager.sendCongratulationsEmail(
                            currentUserEmail,
                            currentUsername,
                            result.workoutType,
                            result.newLeader.score
                        )
                        emailManager.sendBraggingRightsEmail(
                            result.formerLeader.email,
                            result.formerLeader.username,
                            currentUsername,
                            result.workoutType,
                            result.newLeader.score,
                            result.formerLeader.score
                        )
                        onFinished()
                    }
                    3 -> {
                        // Skip - do nothing
                        dialog.dismiss()
                        onFinished()
                    }
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                onFinished()
            }
            .show()
    }

    fun setBackgroundExercise(selectedExercise: Workout) {
        val background =
            when (selectedExercise) {
                Workout.BENCH_PRESS -> R.drawable.bench_press_2
                Workout.CURL -> R.drawable.dumbbell
                Workout.PUSH_UP -> R.drawable.push_up
                Workout.RUN -> R.drawable.footsteps
                Workout.SQUAT -> R.drawable.squat_2
                else -> R.drawable.generic_exercise
            }

        val imageView = findViewById<ImageView>(R.id.backgroundExerciseImage)
        val exerciseNameView = findViewById<TextView>(R.id.exerciseName)
        imageView.setImageResource(background)
        exerciseNameView.text = selectedExercise.displayName
    }

    fun goToLeaderboard() {
        backButton.isEnabled = false
        game.saveToFirebase(this)
        val intent = Intent(this, LeaderboardActivity::class.java)
        intent.putExtra(Utilities.WORKOUT_NAME, workoutName)
        intent.putExtra(Utilities.UNIT, workoutUnit)
        startActivity(intent)
    }

}