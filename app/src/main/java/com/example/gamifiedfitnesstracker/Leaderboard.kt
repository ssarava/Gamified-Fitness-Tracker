package com.example.gamifiedfitnesstracker

import android.annotation.SuppressLint
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener

class Leaderboard {

    private var playersList = ArrayList<Player>()
    private var currentSortMode: Workout
    private var currentUsername: String
    private var leaderboardAdapter: LeaderboardAdapter

    constructor(usernameIn: String, sortModeIn: Workout = Workout.NONE) {
        currentUsername = usernameIn
        currentSortMode = sortModeIn
        leaderboardAdapter = LeaderboardAdapter()
    }

    fun getAdapter() = leaderboardAdapter

    fun setSortMode(mode: Workout) { if (currentSortMode != mode) currentSortMode = mode }

    fun getCurrentSortMode() = currentSortMode

    fun getPlayers() = playersList

    fun getCurrentUsername() = currentUsername

    @SuppressLint("NotifyDataSetChanged")
    fun sortAndUpdateLeaderboard() {
        when (currentSortMode) {
            Workout.BENCH_PRESS -> playersList.sortByDescending { it.bpBest }
            Workout.CURL -> playersList.sortByDescending { it.curlBest }
            Workout.NONE -> return
            Workout.PUSH_UP -> playersList.sortByDescending { it.pushUpBest }
            Workout.RUN -> playersList.sortByDescending { it.runBest }
            Workout.SQUAT -> playersList.sortByDescending { it.squatBest }
            Workout.SWIM -> playersList.sortByDescending { it.swimBest }
        }
        leaderboardAdapter.notifyDataSetChanged()       // update adapter
    }

    /**
     * Load leaderboard data from Firebase Database
     */
    fun loadLeaderboardData(listener: ValueEventListener) {
        val usersRef = Utilities.USERS
        usersRef.addValueEventListener(listener)
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val generic = object : GenericTypeIndicator<HashMap<String, Any>>() {}
                    val users: HashMap<String, Any> = snapshot.getValue(generic)!!

                    for (user in users.keys) {
                        val pbRef = usersRef.child(user).child("Personal Bests")

                        pbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    // Load each exercise's personal best
                                    val bp = snapshot.child(Workout.BENCH_PRESS.displayName)
                                        .getValue(Int::class.java) ?: 0
                                    val curl = snapshot.child(Workout.CURL.displayName)
                                        .getValue(Int::class.java) ?: 0
                                    val pushUp = snapshot.child(Workout.PUSH_UP.displayName)
                                        .getValue(Int::class.java) ?: 0
                                    val run = snapshot.child(Workout.RUN.displayName)
                                        .getValue(Int::class.java) ?: 0
                                    val squat = snapshot.child(Workout.SQUAT.displayName)
                                        .getValue(Int::class.java) ?: 0
                                    val swim = snapshot.child(Workout.SWIM.displayName)
                                        .getValue(Int::class.java) ?: 0

                                    // update playersList
                                    val player = Player(
                                        user,
                                        bp,
                                        curl,
                                        pushUp,
                                        run,
                                        squat,
                                        swim
                                    )
                                    playersList.add(player)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                println("Database Error ${error.code}: ${error.message}")
                            }
                        })
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                println("Database Error ${error.code}: ${error.message}")
            }
        })
    }
}