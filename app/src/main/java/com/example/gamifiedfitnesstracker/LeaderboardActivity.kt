package com.example.gamifiedfitnesstracker

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
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

    private lateinit var rvLeaderboard: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnSortBenchPress: ImageView
    private lateinit var btnSortCurl: ImageView
    private lateinit var btnSortPushUp: ImageView
    private lateinit var btnSortRun: ImageView
    private lateinit var btnSortSquat: ImageView
    private lateinit var btnSortSwim: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        val sp = getSharedPreferences("${packageName}_preferences", MODE_PRIVATE)
        val username = sp.getString(Utilities.PREFERENCE_USERNAME, "")!!

        leaderboard = Leaderboard(username)

        // Initialize UI components
        initializeViews()
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
                        if (player != null && player.username != null) {
                            playersList.add(player)
                        }
                    }

                    if (playersList.isNotEmpty()) {
                        leaderboard.sortAndUpdateLeaderboard()
                        scrollToCurrentUser()
                    }
                }

                showLoading(false)
            }

            override fun onCancelled(error: DatabaseError) {
                showLoading(false)

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
        btnSortBenchPress = findViewById(R.id.btnSortBenchPress)
        btnSortCurl = findViewById(R.id.btnSortCurl)
        btnSortPushUp = findViewById(R.id.btnSortPushUp)
        btnSortRun = findViewById(R.id.btnSortRun)
        btnSortSquat = findViewById(R.id.btnSortSquat)
        btnSortSwim = findViewById(R.id.btnSortSwim)

        // Set initial button states
        updateSortButtonColors()

        // Set up RecyclerView with adapter and layout manager
        rvLeaderboard.layoutManager = LinearLayoutManager(this)
        rvLeaderboard.adapter = leaderboard.getAdapter()
    }

    private fun setupClickListeners() {
        btnSortBenchPress.setOnClickListener(SortButtonListener(Workout.BENCH_PRESS))
        btnSortCurl.setOnClickListener(SortButtonListener(Workout.CURL))
        btnSortPushUp.setOnClickListener(SortButtonListener(Workout.PUSH_UP))
        btnSortRun.setOnClickListener(SortButtonListener(Workout.RUN))
        btnSortSquat.setOnClickListener(SortButtonListener(Workout.SQUAT))
        btnSortSwim.setOnClickListener(SortButtonListener(Workout.SWIM))
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
                    Workout.NONE -> getString(R.string.none_enum)
                    Workout.PUSH_UP -> getString(R.string.pushUp_enum)
                    Workout.RUN -> getString(R.string.run_enum)
                    Workout.SQUAT -> getString(R.string.squat_enum)
                    Workout.SWIM -> getString(R.string.swim_enum)
                }
        }
    }

    private fun highlightImageView(iv: ImageView, shouldHighlight: Boolean = true) {
        val bgColor = if (shouldHighlight) R.color.primary_color else android.R.color.transparent
        iv.setBackgroundColor(ContextCompat.getColor(this, bgColor))
        iv.setBackgroundResource(if (shouldHighlight) R.drawable.sort_button_border else 0)
        iv.alpha = if (shouldHighlight) 1.0f else 0.2f
    }

    /**
     * Update visual state of sorting buttons
     */
    private fun updateSortButtonColors() {
        // reset all buttons
        highlightImageView(btnSortBenchPress, false)
        highlightImageView(btnSortCurl, false)
        highlightImageView(btnSortPushUp, false)
        highlightImageView(btnSortRun, false)
        highlightImageView(btnSortSquat, false)
        highlightImageView(btnSortSwim, false)

        // update only the selected button
        when (leaderboard.getCurrentSortMode()) {
            Workout.BENCH_PRESS -> highlightImageView(btnSortBenchPress)
            Workout.CURL -> highlightImageView(btnSortCurl)
            Workout.NONE -> return
            Workout.PUSH_UP -> highlightImageView(btnSortPushUp)
            Workout.RUN -> highlightImageView(btnSortRun)
            Workout.SQUAT -> highlightImageView(btnSortSquat)
            Workout.SWIM -> highlightImageView(btnSortSwim)
        }
    }

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

    companion object {
        lateinit var leaderboard: Leaderboard
    }
}