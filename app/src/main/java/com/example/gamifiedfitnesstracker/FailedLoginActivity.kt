package com.example.gamifiedfitnesstracker

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class FailedLoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_failed_login)

        // Initialize relevant UI components
        val failedUsername = intent.getStringExtra(Utilities.FAILED_USERNAME)!!
        findViewById<TextView>(R.id.tvUsername).text = failedUsername
        findViewById<MaterialButton>(R.id.btnBack).setOnClickListener { finish() }
    }
}
