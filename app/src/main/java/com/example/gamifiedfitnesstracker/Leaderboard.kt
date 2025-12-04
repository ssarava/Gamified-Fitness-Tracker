package com.example.gamifiedfitnesstracker

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Leaderboard {

    private var playersList = ArrayList<Player>()

    // Data
    private var currentSortMode = SortMode.CALORIES
    private var currentUserId: String = ""
    private var workoutId: String = ""

    // Firebase
    private var database = FirebaseDatabase.getInstance().reference

    // Initialize Firebase
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var leaderboardAdapter: LeaderboardAdapter

    constructor(workoutIdIn: String) {
        currentUserId = auth.currentUser?.uid ?: ""

        leaderboardAdapter = LeaderboardAdapter()

        // Get workout ID from intent
        workoutId = workoutIdIn
    }

    fun getAdapter(): LeaderboardAdapter {
        return leaderboardAdapter
    }

    fun getDatabase() : DatabaseReference {
        return database
    }

    fun getWorkoutId(): String {
        return workoutId
    }

    fun getCurrentUserId() : String {
        return currentUserId
    }

    fun setSortMode(mode: SortMode) {
        if (currentSortMode != mode) currentSortMode = mode
    }

    fun getCurrentSortMode(): SortMode {
        return currentSortMode
    }

    fun getPlayers(): ArrayList<Player> {
        return playersList
    }

    enum class SortMode {
        CALORIES, DURATION
    }

    /**
     * Sort players based on current sort mode and update the adapter
     */
    fun sortAndUpdateLeaderboard() {
        when (currentSortMode) {
            SortMode.CALORIES -> playersList.sortByDescending { it.caloriesBurned }
            SortMode.DURATION -> playersList.sortByDescending { it.workoutDuration }
        }

        // Update ranks after sorting
//        playersList.forEachIndexed { index, player -> player.rank = index + 1 }

        leaderboardAdapter.notifyDataSetChanged()

    }


}