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

    private lateinit var tvUsername: TextView
    private lateinit var btnBack: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_failed_login)

        // Initialize relevant UI components
        tvUsername = findViewById(R.id.tvUsername)
        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener { finish() }     // Return to MainActivity
        tvUsername.text = intent.getStringExtra("USERNAME")!!   // Display error message
    }
}
