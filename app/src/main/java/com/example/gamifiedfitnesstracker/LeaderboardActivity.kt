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
import com.example.gamifiedfitnesstracker.MainActivity.Companion.DATABASE
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class LeaderboardActivity : AppCompatActivity() {

    // UI Components
    private lateinit var rvLeaderboard: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var btnSortBenchPress: Button
    private lateinit var btnSortCurl: Button
    private lateinit var btnSortPushUp: Button
    private lateinit var btnSortRun: Button
    private lateinit var btnSortSquat: Button
    private lateinit var tvHeaderMetric: TextView
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        val username = intent.getStringExtra("USERNAME") ?: ""
        leaderboard = Leaderboard(username)

//        clearTestData()
//        checkAndPopulateIfEmpty()

        // Initialize UI components
        initializeViews()
        setupRecyclerView()
        setupClickListeners()

        // Load leaderboard data
        showLoading(true)
        leaderboard.loadLeaderboardData(CustomValueEventListener())
        leaderboard.sortAndUpdateLeaderboard()


    }

    private fun initializeViews() {
        rvLeaderboard = findViewById(R.id.rvLeaderboard)
        progressBar = findViewById(R.id.progressBar)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)

        btnSortBenchPress = findViewById(R.id.btnSortBenchPress)
        btnSortCurl = findViewById(R.id.btnSortCurl)
        btnSortPushUp = findViewById(R.id.btnSortPushUp)
        btnSortRun = findViewById(R.id.btnSortRun)
        btnSortSquat = findViewById(R.id.btnSortSquat)
        tvHeaderMetric = findViewById(R.id.tvHeaderMetric)
        btnBack = findViewById(R.id.btnBack)

        // Set initial button states
        updateSortButtonColors()
    }

    // Setup RecyclerView with adapter and layout manager
    private fun setupRecyclerView() {
        rvLeaderboard.layoutManager = LinearLayoutManager(this)
        rvLeaderboard.adapter = leaderboard.getAdapter()
    }

    private fun setupClickListeners() {
        btnSortBenchPress.setOnClickListener(SortButtonListener(Workout.BENCH_PRESS))
        btnSortCurl.setOnClickListener(SortButtonListener(Workout.CURL))
        btnSortPushUp.setOnClickListener(SortButtonListener(Workout.PUSH_UP))
        btnSortRun.setOnClickListener(SortButtonListener(Workout.RUN))
        btnSortSquat.setOnClickListener(SortButtonListener(Workout.SQUAT))
        btnBack.setOnClickListener { finish() }
    }

    inner class SortButtonListener : View.OnClickListener {

        private var sortMode: Workout

        constructor(sortModeIn: Workout) {
            sortMode = sortModeIn
        }

        override fun onClick(v: View?) {
            leaderboard.setSortMode(sortMode)
            leaderboard.sortAndUpdateLeaderboard()
            scrollToCurrentUser()
            updateSortButtonColors()
            tvHeaderMetric.text =
                when (sortMode) {
                    Workout.BENCH_PRESS -> getString(R.string.bp_enum)
                    Workout.CURL -> getString(R.string.curl_enum)
                    Workout.NONE -> ""
                    Workout.PUSH_UP -> getString(R.string.pushUp_enum)
                    Workout.RUN -> getString(R.string.run_enum)
                    Workout.SQUAT -> getString(R.string.squat_enum)
                }
        }
    }

    private fun applyToButtons(target: Button, backgroundColor: Int, textColor: Int) {
        target.setBackgroundColor(ContextCompat.getColor(this, backgroundColor))
        target.setTextColor(ContextCompat.getColor(this, textColor))
    }

    /**
     * Update visual state of sorting buttons
     */
    private fun updateSortButtonColors() {
        when (leaderboard.getCurrentSortMode()) {
            Workout.BENCH_PRESS -> {
                applyToButtons(btnSortBenchPress, R.color.primary_color, R.color.white)
                applyToButtons(btnSortCurl, android.R.color.transparent, R.color.primary_color)
                applyToButtons(btnSortPushUp, android.R.color.transparent, R.color.primary_color)
                applyToButtons(btnSortRun, android.R.color.transparent, R.color.primary_color)
                applyToButtons(btnSortSquat, android.R.color.transparent, R.color.primary_color)
            }

            Workout.CURL -> {
                applyToButtons(
                    btnSortBenchPress,
                    android.R.color.transparent,
                    R.color.primary_color
                )
                applyToButtons(btnSortCurl, R.color.primary_color, R.color.white)
                applyToButtons(btnSortPushUp, android.R.color.transparent, R.color.primary_color)
                applyToButtons(btnSortRun, android.R.color.transparent, R.color.primary_color)
                applyToButtons(btnSortSquat, android.R.color.transparent, R.color.primary_color)
            }

            Workout.PUSH_UP -> {
                applyToButtons(
                    btnSortBenchPress,
                    android.R.color.transparent,
                    R.color.primary_color
                )
                applyToButtons(btnSortCurl, android.R.color.transparent, R.color.primary_color)
                applyToButtons(btnSortPushUp, R.color.primary_color, R.color.white)
                applyToButtons(btnSortRun, android.R.color.transparent, R.color.primary_color)
                applyToButtons(btnSortSquat, android.R.color.transparent, R.color.primary_color)
            }

            Workout.NONE -> {
                applyToButtons(
                    btnSortBenchPress,
                    android.R.color.transparent,
                    R.color.primary_color
                )
                applyToButtons(btnSortCurl, android.R.color.transparent, R.color.primary_color)
                applyToButtons(btnSortPushUp, android.R.color.transparent, R.color.primary_color)
                applyToButtons(btnSortRun, android.R.color.transparent, R.color.primary_color)
                applyToButtons(btnSortSquat, android.R.color.transparent, R.color.primary_color)
            }

            Workout.RUN -> {
                applyToButtons(
                    btnSortBenchPress,
                    android.R.color.transparent,
                    R.color.primary_color
                )
                applyToButtons(btnSortCurl, android.R.color.transparent, R.color.primary_color)
                applyToButtons(btnSortPushUp, android.R.color.transparent, R.color.primary_color)
                applyToButtons(btnSortRun, R.color.primary_color, R.color.white)
                applyToButtons(btnSortSquat, android.R.color.transparent, R.color.primary_color)
            }

            Workout.SQUAT -> {
                applyToButtons(
                    btnSortBenchPress,
                    android.R.color.transparent,
                    R.color.primary_color
                )
                applyToButtons(btnSortCurl, android.R.color.transparent, R.color.primary_color)
                applyToButtons(btnSortPushUp, android.R.color.transparent, R.color.primary_color)
                applyToButtons(btnSortRun, android.R.color.transparent, R.color.primary_color)
                applyToButtons(btnSortSquat, R.color.primary_color, R.color.white)
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
                        it.username = playerSnapshot.key ?: ""
                        if (it.runBest != null) playersList.add(it)
                    }
                }

                if (playersList.isNotEmpty()) {
                    leaderboard.sortAndUpdateLeaderboard()
                    scrollToCurrentUser()
                }
                showEmptyState(false)
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
     * Scroll RecyclerView to show current user's position
     */
    private fun scrollToCurrentUser() {
        val currentUsername = intent.getStringExtra("USERNAME")!!
        val currentUserIndex =
            leaderboard.getPlayers().indexOfFirst { it.username == currentUsername }
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
//    private fun populateTestData() {
//        val workoutId = intent.getStringExtra("WORKOUT_ID") ?: "test_workout_1"
//        val workoutRef =
//            DATABASE.child("workouts").child(workoutId).child("players")
//
//        // Create a list of test players with varied data
//        val testPlayers = ArrayList<Player>()
//        for (i in 0..4) {
//            testPlayers.add(
//                Player(
//                    "test_user_$i",
//                    "SpeedyRunner$i",
//                    (i + 1) * 100 + i * 10,
//                    (i + 1) * 60000,
//                    hashMapOf(
////                        Workout.SQUAT to (i + 1) * 20 + i * 4,
////                        Workout.PUSH_UP to (i + 1) * 15 + i * 4,
//                        Workout.RUN to i + 7
//                    )
//                )
//            )
//        }
//
//        // Upload each test player to Firebase
//        testPlayers.forEach { player ->
//            val playerData = hashMapOf(
//                "username" to player.username,
//                "caloriesBurned" to player.caloriesBurned,
//                "workoutDuration" to player.workoutDuration,
//                "workoutRecords" to hashMapOf(
////                    "squatRecord" to player.workoutRecords.get(Workout.SQUAT),
////                    "push_upRecord" to player.workoutRecords.get(Workout.PUSH_UP),
//                    "runRecord" to player.workoutRecords.get(Workout.RUN)
//                )
//            )
//
//            workoutRef.child(player.userId!!).setValue(playerData)
//                .addOnSuccessListener {
//                    Log.d("LeaderboardTest", "Added test user: ${player.username}")
//                }
//                .addOnFailureListener { e ->
//                    Log.e("LeaderboardTest", "Error adding test user: ${e.message}")
//                }
//        }
//
//        Toast.makeText(
//            this,
//            "Test data populated! Refresh to see leaderboard.",
//            Toast.LENGTH_SHORT
//        ).show()
//    }

    /**
     * Clear all test data from Firebase (useful for cleanup)
     */
    private fun clearTestData() {
        val workoutId = intent.getStringExtra("WORKOUT_ID") ?: "test_workout_1"
        val workoutRef =
            DATABASE.child("workouts").child(workoutId).child("players")

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
            DATABASE.child("workouts").child(workoutId).child("players")

        workoutRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists() || snapshot.childrenCount == 0L) {
                    Log.d("LeaderboardTest", "Database empty, populating test data...")
//                    populateTestData()
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