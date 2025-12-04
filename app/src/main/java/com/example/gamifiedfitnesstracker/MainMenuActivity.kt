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
    private lateinit var welcomeTV: TextView
    private lateinit var titleTV: TextView
    private lateinit var squatBestTV: TextView
    private lateinit var pushUpBestTV: TextView
    private lateinit var runningBestTV: TextView
    private lateinit var benchPressBestTV: TextView
    private lateinit var curlBestTV: TextView
    private val database = FirebaseDatabase.getInstance().reference

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
        val username = intent.getStringExtra("USERNAME")!!      // Get username from intent
        welcomeTV.text = resources.getString(R.string.named_welcome, username)
    }

    private fun loadPersonalBestsFromFirebase() {
        val username = intent.getStringExtra("USERNAME")!!
        val personalBestsRef = database.child("users")
            .child(username)
            .child("personalBests")

        personalBestsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val cls = Int::class.java

                    // Load each exercise's personal best
                    val squat = snapshot.child("squat").getValue(cls) ?: 0
                    val pushUp = snapshot.child("pushUp").getValue(cls) ?: 0
                    val run = snapshot.child("running").getValue(cls) ?: 0
                    val bp = snapshot.child("benchPress").getValue(cls) ?: 0
                    val curl = snapshot.child("curl").getValue(cls) ?: 0

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

    private fun setupClickListeners() = btnPlayGame.setOnClickListener { goToExerciseLogger() }

    private fun goToExerciseLogger() {
        val intent = Intent(this, ExerciseLoggerActivity::class.java)
        intent.putExtra("USERNAME", intent.getStringExtra("USERNAME")!!)
        startActivity(intent)
        // no finish() since we want to return to the main menu maybe via nathan's screen
    }

    // refresh personal bests when returning to this activity
    override fun onResume() {
        super.onResume()
        loadPersonalBestsFromFirebase()
    }
}