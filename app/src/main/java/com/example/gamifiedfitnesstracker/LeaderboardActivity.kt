package com.example.gamifiedfitnesstracker

import android.os.Bundle
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

//        clearTestData("insert_username")
//        populateTestData()

        // Initialize UI components
        initializeViews()
        setupRecyclerView()
        setupClickListeners()

        // Load leaderboard data
        showLoading(true)
        leaderboard.loadLeaderboardData(object : ValueEventListener {
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
        })
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

    private fun highlightButton(button: Button, shouldHighlight: Boolean = true) {
        val bgColor = if (shouldHighlight) R.color.primary_color else android.R.color.transparent
        val textColor = if (shouldHighlight) R.color.white else R.color.primary_color
        button.setBackgroundColor(ContextCompat.getColor(this, bgColor))
        button.setTextColor(ContextCompat.getColor(this, textColor))
    }

    /**
     * Update visual state of sorting buttons
     */
    private fun updateSortButtonColors() {
        // reset all buttons
        highlightButton(btnSortBenchPress, false)
        highlightButton(btnSortCurl, false)
        highlightButton(btnSortPushUp, false)
        highlightButton(btnSortRun, false)
        highlightButton(btnSortSquat, false)

        // update only the selected button
        when (leaderboard.getCurrentSortMode()) {
            Workout.BENCH_PRESS -> highlightButton(btnSortBenchPress)
            Workout.CURL -> highlightButton(btnSortCurl)
            Workout.PUSH_UP -> highlightButton(btnSortPushUp)
            Workout.NONE -> return
            Workout.RUN -> highlightButton(btnSortRun)
            Workout.SQUAT -> highlightButton(btnSortSquat)
        }
    }

    /**
     * Scroll RecyclerView to show current user's position
     */
    private fun scrollToCurrentUser() {
        val currentUsername = intent.getStringExtra("USERNAME")!!
        val currentUserIndex =
            leaderboard.getPlayers().indexOfFirst { it.username == currentUsername }
        if (currentUserIndex != -1) rvLeaderboard.scrollToPosition(currentUserIndex)
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

    /**
     * If no argument is specified, clears all test data from Firebase (useful for cleanup);
     * otherwise, only removes the specified users, if they exists
     */
    private fun clearTestData(vararg usernames: String) {
        for (username in usernames) {
            DATABASE.child("users").child(username).removeValue()
        }
    }

    private fun populateTestData(numOfUsers: Int = 3) {
        for (i in 1 .. numOfUsers) {
            val username = "random_user_$i"
            val pw = "random_password_$i"
            val newUser = MainActivity.createNewUser(username, pw, true)

            DATABASE.child("users").child(username).setValue(newUser)
                .addOnSuccessListener { println("Successfully added user $username") }
                .addOnFailureListener { e -> println("Failed to create account: ${e.message}") }
        }

    }

    companion object {
        lateinit var leaderboard: Leaderboard
    }
}