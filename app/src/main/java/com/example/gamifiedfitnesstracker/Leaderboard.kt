package com.example.gamifiedfitnesstracker

import android.util.Log
import com.example.gamifiedfitnesstracker.MainActivity.Companion.DATABASE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener

class Leaderboard {

    private var playersList = ArrayList<Player>()

    // Data
    private var currentSortMode = SortMode.NONE
    private var currentUsername: String

    // Initialize Firebase
    private var leaderboardAdapter: LeaderboardAdapter

    constructor(usernameIn: String) {
        // Get current user from intent
        currentUsername = usernameIn
        leaderboardAdapter = LeaderboardAdapter()
    }

    fun getAdapter() = leaderboardAdapter

    fun setSortMode(mode: SortMode) {
        if (currentSortMode != mode) currentSortMode = mode
    }

    fun getCurrentSortMode() = currentSortMode

    fun getPlayers() = playersList

    enum class SortMode {
        NONE, RUN, SQUAT
    }

    fun getCurrentUsername() = currentUsername

    /**
     * Sort players based on current sort mode and update the adapter
     */
    fun sortAndUpdateLeaderboard() {
        // needs updating
        when (currentSortMode) {
            SortMode.NONE -> return
            SortMode.RUN -> playersList.sortByDescending { it.runBest }
            SortMode.SQUAT -> playersList.sortByDescending { it.squatBest }
        }
        leaderboardAdapter.notifyDataSetChanged()
    }

    /**
     * Load leaderboard data from Firebase Database
     */
    fun loadLeaderboardData(listener: ValueEventListener) {
        val leaderboardRef = DATABASE.child("users")
        leaderboardRef.addValueEventListener(listener)

        val usersRef = DATABASE.child("users")
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val generic = object : GenericTypeIndicator<HashMap<String, Any>>() {}
                    val users: HashMap<String, Any> = snapshot.getValue(generic)!!

                    for (user in users.keys.toList()) {
                        val personalBestsRef = usersRef.child(user).child("personalBests")

                        personalBestsRef.addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    val cls = Int::class.java

                                    // Load each exercise's personal best
                                    val squat = snapshot.child("squat").getValue(cls) ?: 0
                                    val pushUp = snapshot.child("pushUp").getValue(cls) ?: 0
                                    val run = snapshot.child("running").getValue(cls) ?: 0
                                    val bp = snapshot.child("benchPress").getValue(cls) ?: 0
                                    val curl = snapshot.child("curl").getValue(cls) ?: 0

                                    // update playersList
                                    val player = Player(
                                        user,
                                        bp,
                                        curl,
                                        pushUp,
                                        run,
                                        squat
                                    )
                                    playersList.add(player)
                                    // Update UI
//                    updatePersonalBestsUI(squat, pushUp, run, bp, curl)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                println("Database Error ${error.code}: ${error.message}")   // error debug message
                            }
                        })
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}