package com.example.gamifiedfitnesstracker

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainMenuActivity : AppCompatActivity() {

    private lateinit var btnPlayGame: MaterialButton
    private lateinit var welcomeTextView: TextView
    private lateinit var titleTextView: TextView
    private lateinit var squatBestTextView: TextView
    private lateinit var pushupBestTextView: TextView
    private lateinit var runningBestTextView: TextView
    private lateinit var benchpressBestTextView: TextView
    private lateinit var curlBestTextView: TextView

    private lateinit var username: String
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        initializeViews()
        getUsername()
        displayWelcomeMessage()
        loadPersonalBestsFromFirebase()
        setupClickListeners()
    }

    private fun initializeViews() {
        btnPlayGame = findViewById(R.id.btnPlayGame)
        welcomeTextView = findViewById(R.id.welcomeTextView)
        titleTextView = findViewById(R.id.titleTextView)
        squatBestTextView = findViewById(R.id.squatBestTextView)
        pushupBestTextView = findViewById(R.id.pushupBestTextView)
        runningBestTextView = findViewById(R.id.runningBestTextView)
        benchpressBestTextView = findViewById(R.id.benchpressBestTextView)
        curlBestTextView = findViewById(R.id.curlBestTextView)

        // Set the title directly
        titleTextView.text = "My Fitness Journey"
    }

    private fun getUsername() {
        // Get username from intent first
        username = intent.getStringExtra("USERNAME") ?: ""
    }

    private fun displayWelcomeMessage() {
        if (username.isNotEmpty()) {
            // Personalized welcome message
            welcomeTextView.text = "Welcome, $username!"
        } else {
            // Default message if no username found
            welcomeTextView.text = "Welcome to your fitness journey!"
        }
    }

    private fun loadPersonalBestsFromFirebase() {
        if (username.isEmpty()) {
            return // Can't load without username
        }

        val personalBestsRef = database.child("users")
            .child(username)
            .child("personalBests")

        personalBestsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Load each exercise's personal best
                    val squatBest = snapshot.child("squat").getValue(Int::class.java) ?: 0
                    val pushupBest = snapshot.child("pushup").getValue(Int::class.java) ?: 0
                    val runningBest = snapshot.child("running").getValue(Int::class.java) ?: 0
                    val benchpressBest = snapshot.child("benchpress").getValue(Int::class.java) ?: 0
                    val curlBest = snapshot.child("curl").getValue(Int::class.java) ?: 0

                    // Update UI
                    updatePersonalBestsUI(squatBest, pushupBest, runningBest, benchpressBest, curlBest)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // basic error debug message
                println("Firebase Database Error: ${error.code} - ${error.message}")            }
        })
    }

    private fun updatePersonalBestsUI(
        squat: Int,
        pushup: Int,
        running: Int,
        benchpress: Int,
        curl: Int
    ) {
        runOnUiThread {
            squatBestTextView.text = squat.toString()
            pushupBestTextView.text = pushup.toString()
            runningBestTextView.text = running.toString()
            benchpressBestTextView.text = benchpress.toString()
            curlBestTextView.text = curl.toString()
        }
    }

    private fun setupClickListeners() {
        btnPlayGame.setOnClickListener {
            navigateToExerciseLogger()
        }
    }

    private fun navigateToExerciseLogger() {
        val intent = Intent(this, ExerciseLoggerActivity::class.java)

        if (username.isNotEmpty()) {
            intent.putExtra("USERNAME", username)
        }

        startActivity(intent)
        // no finish() since we want to ideally be able to return to the main menu maybe via nathan's screen
    }

    // refresh personal bests when returning to this activity
    override fun onResume() {
        super.onResume()
        if (username.isNotEmpty()) {
            loadPersonalBestsFromFirebase()
        }
    }
}