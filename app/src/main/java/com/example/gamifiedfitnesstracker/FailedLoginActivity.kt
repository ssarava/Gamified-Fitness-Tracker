package com.example.gamifiedfitnesstracker

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

/**
 * FailedLoginActivity displays an error message when a user enters
 * an existing username with an incorrect password.
 */
class FailedLoginActivity : AppCompatActivity() {

    // UI Components
    private lateinit var tvErrorMessage: TextView
    private lateinit var tvUsername: TextView
    private lateinit var btnBack: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_failed_login)

        initializeViews()   // Initialize UI components
        val username = intent.getStringExtra("USERNAME") ?: "Unknown"   // Get username
        displayErrorMessage(username)   // Display error message with username
    }

    /**
     * Initialize all UI components
     */
    private fun initializeViews() {
        tvErrorMessage = findViewById(R.id.tvErrorMessage)
        tvUsername = findViewById(R.id.tvUsername)
        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener { finish() }     // Finish this activity, return to LoginActivity
    }

    /**
     * Display error message with the username
     */
    private fun displayErrorMessage(username: String) {
        // Set the main error message
        tvErrorMessage.text = "User with username"

        // Display the username prominently
        tvUsername.text = username

        // Alternative: Can combine them in a single TextView if preferred
        // tvErrorMessage.text = "User with $username exists but an incorrect password was entered"
    }
}
