package com.example.gamifiedfitnesstracker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
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
    private lateinit var btnSortBenchPress: Button
    private lateinit var btnSortCurl: Button
    private lateinit var btnSortPushUp: Button
    private lateinit var btnSortRun: Button
    private lateinit var btnSortSquat: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        val sp = getSharedPreferences("${packageName}_preferences", MODE_PRIVATE)
        val username = sp.getString(Utilities.PREFERENCE_USERNAME, "")!!

        leaderboard = Leaderboard(username)

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
                Utilities.initializeToast(
                    this@LeaderboardActivity,
                    "Error loading leaderboard: ${error.message}"
                )
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
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
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
            findViewById<TextView>(R.id.tvHeaderMetric).text =
                when (sortMode) {
                    Workout.BENCH_PRESS -> getString(R.string.bp_enum)
                    Workout.CURL -> getString(R.string.curl_enum)
                    Workout.CUSTOM -> TODO()
                    Workout.DEFAULT -> TODO()
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
            Workout.RUN -> highlightButton(btnSortRun)
            Workout.SQUAT -> highlightButton(btnSortSquat)
            else -> return
        }
    }

    /**
     * Scroll RecyclerView to show current user's position
     */
    private fun scrollToCurrentUser() {
        val sp = getSharedPreferences("${packageName}_preferences", MODE_PRIVATE)
        val username = sp.getString(Utilities.PREFERENCE_USERNAME, "")
        val currentUserIndex =
            leaderboard.getPlayers().indexOfFirst { it.username == username }
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

    companion object {
        lateinit var leaderboard: Leaderboard
    }
}