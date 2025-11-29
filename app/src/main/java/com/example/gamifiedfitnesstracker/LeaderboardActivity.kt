package com.example.gamifiedfitnesstracker

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class LeaderboardActivity : AppCompatActivity() {

    // UI Components
    private lateinit var rvLeaderboard: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var btnSortCalories: Button
    private lateinit var btnSortDuration: Button
    private lateinit var tvHeaderMetric: TextView
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        val workoutIdIn = intent.getStringExtra("WORKOUT_ID") ?: ""
        leaderboard = Leaderboard(workoutIdIn)

        clearTestData()
        checkAndPopulateIfEmpty()

        // Initialize UI components
        initializeViews()
        setupRecyclerView()
        setupClickListeners()

        // Load leaderboard data
        loadLeaderboardData()
    }

    /**
     * Initialize all UI components
     */
    private fun initializeViews() {
        rvLeaderboard = findViewById(R.id.rvLeaderboard)
        progressBar = findViewById(R.id.progressBar)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        btnSortCalories = findViewById(R.id.btnSortCalories)
        btnSortDuration = findViewById(R.id.btnSortDuration)
        tvHeaderMetric = findViewById(R.id.tvHeaderMetric)
        btnBack = findViewById(R.id.btnBack)

        // Set initial button states
        updateSortButtonColors()
    }

    /**
     * Setup RecyclerView with adapter and layout manager
     */
    private fun setupRecyclerView() {
        rvLeaderboard.layoutManager = LinearLayoutManager(this)
        rvLeaderboard.adapter = leaderboard.getAdapter()
    }

    /**
     * Setup click listeners for sorting buttons
     */
    private fun setupClickListeners() {
        btnSortCalories.setOnClickListener(SortButtonListener(Leaderboard.SortMode.CALORIES))
        btnSortDuration.setOnClickListener(SortButtonListener(Leaderboard.SortMode.DURATION))
        btnBack.setOnClickListener { finish() }
    }

    inner class SortButtonListener : View.OnClickListener {

        private var sortMode: Leaderboard.SortMode

        constructor(sortModeIn: Leaderboard.SortMode) {
            sortMode = sortModeIn
        }

        override fun onClick(v: View?) {
            leaderboard.setSortMode(sortMode)
            leaderboard.sortAndUpdateLeaderboard()
            scrollToCurrentUser()
            updateSortButtonColors()
            tvHeaderMetric.text =
                getString(if (sortMode == Leaderboard.SortMode.CALORIES) R.string.calories else R.string.duration)
//            Log.w(
//                "MainActivity",
//                "$sortMode clicked\t current sort mode = ${leaderboard.getCurrentSortMode()}"
//            )
        }

    }

    /**
     * Update visual state of sorting buttons
     */
    private fun updateSortButtonColors() {
        when (leaderboard.getCurrentSortMode()) {
            Leaderboard.SortMode.CALORIES -> {
                btnSortCalories.apply {
                    setBackgroundColor(
                        ContextCompat.getColor(
                            this@LeaderboardActivity,
                            R.color.primary_color
                        )
                    )
                    setTextColor(ContextCompat.getColor(this@LeaderboardActivity, R.color.white))
                }
                btnSortDuration.apply {
                    setBackgroundColor(
                        ContextCompat.getColor(
                            this@LeaderboardActivity,
                            android.R.color.transparent
                        )
                    )
                    setTextColor(
                        ContextCompat.getColor(
                            this@LeaderboardActivity,
                            R.color.primary_color
                        )
                    )
                }
            }

            Leaderboard.SortMode.DURATION -> {
                btnSortCalories.apply {
                    setBackgroundColor(
                        ContextCompat.getColor(
                            this@LeaderboardActivity,
                            android.R.color.transparent
                        )
                    )
                    setTextColor(
                        ContextCompat.getColor(
                            this@LeaderboardActivity,
                            R.color.primary_color
                        )
                    )
                }
                btnSortDuration.apply {
                    setBackgroundColor(
                        ContextCompat.getColor(
                            this@LeaderboardActivity,
                            R.color.primary_color
                        )
                    )
                    setTextColor(ContextCompat.getColor(this@LeaderboardActivity, R.color.white))
                }
            }
        }
    }

    inner class CustomValueEventListener : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val playersList = leaderboard.getPlayers()
            playersList.clear()

            if (snapshot.exists()) {
                for (playerSnapshot in snapshot.children) {
                    val player = playerSnapshot.getValue(Player::class.java)
                    player?.let {
                        it.userId = playerSnapshot.key ?: ""
                        playersList.add(it)
                    }
                }

                if (playersList.isNotEmpty()) {
                    leaderboard.sortAndUpdateLeaderboard()
                    scrollToCurrentUser()
                    showEmptyState(false)
                } else {
                    showEmptyState(true)
                }
            } else {
                showEmptyState(true)
            }

            showLoading(false)
        }

        override fun onCancelled(error: DatabaseError) {
            showLoading(false)
            showEmptyState(true)

            // Handle error (you can show a toast or snack bar)
            Toast.makeText(
                this@LeaderboardActivity,
                "Error loading leaderboard: ${error.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Load leaderboard data from Firebase Database
     */
    private fun loadLeaderboardData() {
        showLoading(true)

        // Reference to the workout leaderboard in Firebase
        // Structure: workouts/{workoutId}/players/{userId}
        val database = leaderboard.getDatabase()
        val workoutId = leaderboard.getWorkoutId()
        val leaderboardRef = database.child("workouts").child(workoutId).child("players")
        leaderboardRef.addValueEventListener(CustomValueEventListener())
    }

    /**
     * Scroll RecyclerView to show current user's position
     */
    private fun scrollToCurrentUser() {
        val currentUserIndex =
            leaderboard.getPlayers().indexOfFirst { it.userId == leaderboard.getCurrentUserId() }
        if (currentUserIndex != -1) {
            rvLeaderboard.scrollToPosition(currentUserIndex)
        }
    }

    /**
     * Show or hide loading indicator
     */
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        rvLeaderboard.visibility = if (show) View.GONE else View.VISIBLE
    }

    /**
     * Show or hide empty state
     */
    private fun showEmptyState(show: Boolean) {
        emptyStateLayout.visibility = if (show) View.VISIBLE else View.GONE
        rvLeaderboard.visibility = if (show) View.GONE else View.VISIBLE
    }

    companion object {
        lateinit var leaderboard: Leaderboard
    }

    /**
     * Populate Firebase Database with sample test data for leaderboard testing.
     * Call this method from onCreate() to add test users.
     *
     * IMPORTANT: Remove or comment out this function before production release!
     */
    private fun populateTestData() {
        val workoutId = intent.getStringExtra("WORKOUT_ID") ?: "test_workout_1"
        val workoutRef =
            leaderboard.getDatabase().child("workouts").child(workoutId).child("players")

        // Create a list of test players with varied data
        val testPlayers = ArrayList<Player>()
        for (i in 0..4) {
            testPlayers.add(
                Player(
                    "test_user_$i",
                    "SpeedyRunner$i",
                    (i + 1) * 100 + i * 10,
                    (i + 1) * 60000,
                    hashMapOf(
//                        Workout.SQUAT to (i + 1) * 20 + i * 4,
//                        Workout.PUSH_UP to (i + 1) * 15 + i * 4,
                        Workout.RUN to i + 7
                    )
                )
            )
        }

        // Upload each test player to Firebase
        testPlayers.forEach { player ->
            val playerData = hashMapOf(
                "username" to player.username,
                "caloriesBurned" to player.caloriesBurned,
                "workoutDuration" to player.workoutDuration,
                "workoutRecords" to hashMapOf(
//                    "squatRecord" to player.workoutRecords.get(Workout.SQUAT),
//                    "push_upRecord" to player.workoutRecords.get(Workout.PUSH_UP),
                    "runRecord" to player.workoutRecords.get(Workout.RUN)
                )
            )

            workoutRef.child(player.userId!!).setValue(playerData)
                .addOnSuccessListener {
                    Log.d("LeaderboardTest", "Added test user: ${player.username}")
                }
                .addOnFailureListener { e ->
                    Log.e("LeaderboardTest", "Error adding test user: ${e.message}")
                }
        }

        Toast.makeText(
            this,
            "Test data populated! Refresh to see leaderboard.",
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Clear all test data from Firebase (useful for cleanup)
     */
    private fun clearTestData() {
        val workoutId = intent.getStringExtra("WORKOUT_ID") ?: "test_workout_1"
        val workoutRef =
            leaderboard.getDatabase().child("workouts").child(workoutId).child("players")

        workoutRef.removeValue()
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Test data cleared!",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("LeaderboardTest", "Test data cleared successfully")
            }
            .addOnFailureListener { e ->
                Log.e("LeaderboardTest", "Error clearing test data: ${e.message}")
            }
    }

    /**
     * Check if the leaderboard is empty and populate with test data if needed
     */
    private fun checkAndPopulateIfEmpty() {
        val workoutId = intent.getStringExtra("WORKOUT_ID") ?: "test_workout_1"
        val workoutRef =
            leaderboard.getDatabase().child("workouts").child(workoutId).child("players")

        workoutRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists() || snapshot.childrenCount == 0L) {
                    Log.d("LeaderboardTest", "Database empty, populating test data...")
                    populateTestData()
                } else {
                    Log.d(
                        "LeaderboardTest",
                        "Database has data, skipping test population"
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("LeaderboardTest", "Error checking database: ${error.message}")
            }
        })
    }
}