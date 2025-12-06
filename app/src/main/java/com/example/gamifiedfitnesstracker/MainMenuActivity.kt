package com.example.gamifiedfitnesstracker

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MainMenuActivity : AppCompatActivity() {

    private lateinit var btnPlayGame: MaterialButton
    private lateinit var welcomeTV: TextView
    private lateinit var titleTV: TextView
    private lateinit var squatBestTV: TextView
    private lateinit var pushUpBestTV: TextView
    private lateinit var runningBestTV: TextView
    private lateinit var benchPressBestTV: TextView
    private lateinit var curlBestTV: TextView
    private lateinit var userName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        initializeViews()
        setUsernameAndWelcomeMessage()
        loadPersonalBestsFromFirebase()
        setupClickListeners()
    }

    private fun initializeViews() {
        btnPlayGame = findViewById(R.id.btnPlayGame)
        welcomeTV = findViewById(R.id.welcomeTextView)
        titleTV = findViewById(R.id.titleTextView)
        squatBestTV = findViewById(R.id.squatBestTextView)
        pushUpBestTV = findViewById(R.id.pushUpBestTextView)
        runningBestTV = findViewById(R.id.runningBestTextView)
        benchPressBestTV = findViewById(R.id.benchPressBestTextView)
        curlBestTV = findViewById(R.id.curlBestTextView)
        titleTV.text = resources.getString(R.string.my_fitness_journey)
    }

    private fun setUsernameAndWelcomeMessage() {
        val sp = getSharedPreferences("${packageName}_preferences", MODE_PRIVATE)
        userName = sp.getString(Utilities.PREFERENCE_USERNAME, "")!!
        welcomeTV.text = resources.getString(R.string.named_welcome, userName)
    }

    private fun loadPersonalBestsFromFirebase() {
        val sp = getSharedPreferences("${packageName}_preferences", MODE_PRIVATE)
        val username = sp.getString(Utilities.PREFERENCE_USERNAME, "")!!
        val personalBestsRef = Utilities.USERS
            .child(username)
            .child("Personal Bests")

        personalBestsRef.addListenerForSingleValueEvent(object : ValueEventListener {
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

                    // Update UI
                    updatePersonalBestsUI(squat, pushUp, run, bp, curl)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Database Error ${error.code}: ${error.message}")   // error debug message
            }
        })
    }


    private fun updatePersonalBestsUI(squat: Int, pushUp: Int, run: Int, bp: Int, curl: Int) {
        runOnUiThread {
            squatBestTV.text = squat.toString()
            pushUpBestTV.text = pushUp.toString()
            runningBestTV.text = run.toString()
            benchPressBestTV.text = bp.toString()
            curlBestTV.text = curl.toString()
        }
    }

    private fun setupClickListeners() = btnPlayGame.setOnClickListener { goToSelectWorkout() }

    private fun goToSelectWorkout() {
        val intent = Intent(this, SelectWorkoutActivity::class.java)

        // send personal bests to next activity
        intent.putExtra(Utilities.BEST_BENCH, benchPressBestTV.text)
        intent.putExtra(Utilities.BEST_CURL, curlBestTV.text)
        intent.putExtra(Utilities.BEST_PUSH_UP, pushUpBestTV.text)
        intent.putExtra(Utilities.BEST_RUN, runningBestTV.text)
        intent.putExtra(Utilities.BEST_SQUAT, squatBestTV.text)
        startActivity(intent)
        // no finish() since we want to return to the main menu maybe via nathan's screen
    }

    // refresh personal bests when returning to this activity
    override fun onResume() {
        super.onResume()
        loadPersonalBestsFromFirebase()
    }
}