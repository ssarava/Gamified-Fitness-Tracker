package com.example.gamifiedfitnesstracker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*
import androidx.core.content.edit

/**
 * Handles user authentication with username and password.
 * User credentials are stored in Firebase.
 */
class MainActivity : AppCompatActivity() {

    // UI Components
    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var usernameLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var btnLogin: MaterialButton
    private lateinit var btnSignUp: MaterialButton
    private lateinit var progressBar: ProgressBar

    // Firebase
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance().reference

        // Initialize UI components
        initializeViews()
        setupClickListeners()
    }

    /**
     * Initialize all UI components, and auto-populate username and password if possible
     */
    private fun initializeViews() {
        val sp: SharedPreferences =
            getSharedPreferences("${this.packageName}_preferences", MODE_PRIVATE)

        etUsername = findViewById(R.id.etUsername)
        val storedUsername = sp.getString(PREFERENCE_USERNAME, "")
        etUsername.setText(storedUsername)

        etPassword = findViewById(R.id.etPassword)
        val storedPassword = sp.getString(PREFERENCE_PASSWORD, "")
        etPassword.setText(storedPassword)

        usernameLayout = findViewById(R.id.usernameLayout)
        passwordLayout = findViewById(R.id.passwordLayout)
        btnLogin = findViewById(R.id.btnLogin)
        btnLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_color))
        btnSignUp = findViewById(R.id.btnSignUp)
        btnSignUp.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_color))
        progressBar = findViewById(R.id.progressBar)
    }

    /**
     * Setup click listeners for buttons
     */
    private fun setupClickListeners() {

        btnLogin.setOnClickListener {
            if (validCredentials()) {
//                val username = etUsername.text.toString().trim()
//                val password = etPassword.text.toString().trim()
                loginUser()
            }
        }

        btnSignUp.setOnClickListener {
            if (validCredentials()) {
//                val username = etUsername.text.toString().trim()
//                val password = etPassword.text.toString().trim()
                signUpUser()
            }
        }
    }

    /**
     * Validate user input
     */
    private fun validCredentials(): Boolean {
        var isValid = true
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Clear previous errors
        usernameLayout.error = null
        passwordLayout.error = null

        // Validate username
        if (username.isEmpty()) {
            usernameLayout.error = "Username is required"
            isValid = false
        } else if (username.length < 3) {
            usernameLayout.error = "Username must be at least 3 characters"
            isValid = false
        } else if (!username.matches(Regex("^[a-zA-Z0-9_]+$"))) {
            usernameLayout.error = "Username can only contain letters, numbers, and underscores"
            isValid = false
        }

        // Validate password
        if (password.isEmpty()) {
            passwordLayout.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            passwordLayout.error = "Password must be at least 6 characters"
            isValid = false
        }

        return isValid
    }

    /**
     * Login existing user
     */
    private fun loginUser() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        showLoading(true)

        // Reference to users in Firebase
        val usersRef = database.child("users").child(username)

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // User exists, check password
                    val storedPassword = snapshot.child("password").getValue(String::class.java)

                    // Password correct - successful login
                    if (storedPassword == password) {
                        showLoading(false)
                        Toast.makeText(
                            this@MainActivity,
                            "Login successful! Welcome back, $username",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Save login state
                        saveLoginState(this@MainActivity, username, password)

                        // Navigate to the next activity
                        navigateToSomeActivity()
                    }

                    // Password incorrect - navigate to FailedLoginActivity
                    else {
                        showLoading(false)
                        navigateToFailedLogin(username)
                    }
                } else {
                    // User doesn't exist
                    showLoading(false)
                    usernameLayout.error = "Username not found. Please sign up first."
                    Toast.makeText(
                        this@MainActivity,
                        "Username not found. Please sign up first.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showLoading(false)
                Toast.makeText(
                    this@MainActivity,
                    "Error: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    /**
     * Sign up new user
     */
    private fun signUpUser() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()
        showLoading(true)

        // Check if username already exists
        val usersRef = database.child("users").child(username)

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Username already taken
                    showLoading(false)
                    usernameLayout.error = "Username already taken"
                    Toast.makeText(
                        this@MainActivity,
                        "Username already exists. Please log in or choose a different username",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Username available, create new user
                    createNewUser(username, password)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showLoading(false)
                Toast.makeText(
                    this@MainActivity,
                    "Error: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    /**
     * Create new user in Firebase Database
     */
    private fun createNewUser(username: String, password: String) {
        val userData = hashMapOf(
            "username" to username,
            "password" to password,
            "createdAt" to System.currentTimeMillis(),
            "profileImageUrl" to "",
            "totalWorkouts" to 0,
            "totalCaloriesBurned" to 0
        )

        database.child("users").child(username).setValue(userData)
            .addOnSuccessListener {
                showLoading(false)
                Toast.makeText(
                    this,
                    "Account created successfully! Welcome, $username",
                    Toast.LENGTH_SHORT
                ).show()

                // Save login state
                saveLoginState(this@MainActivity, username, password)

                // Navigate to the next activity
                navigateToSomeActivity()
            }
            .addOnFailureListener { e ->
                showLoading(false)
                Toast.makeText(
                    this,
                    "Failed to create account: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    /**
     * Save login state to SharedPreferences
     */
    private fun saveLoginState(context: Context, username: String, password: String) {
        val sp: SharedPreferences =
            context.getSharedPreferences("${context.packageName}_preferences", MODE_PRIVATE)
        sp.edit(commit = true) {
            putString(PREFERENCE_USERNAME, username)
            putString(PREFERENCE_PASSWORD, password)
            putBoolean("isLoggedIn", true)      // not used in anything else so far
        }
    }

    /**
     * Navigate to FailedLoginActivity
     */
    private fun navigateToFailedLogin(username: String) {
        val intent = Intent(this, FailedLoginActivity::class.java)
        intent.putExtra("USERNAME", username)
        startActivity(intent)
    }

    /**
     * Navigate to the next activity after successful login
     */
    private fun navigateToSomeActivity() {
        // TODO: Replace with your actual main activity

        val intent = Intent(
            this,
            LeaderboardActivity::class.java
        )      // need to change 'LeaderboardActivity' to something else
        startActivity(intent)
        finish()
    }

    /**
     * Show or hide loading indicator
     */
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnLogin.isEnabled = !show
        btnSignUp.isEnabled = !show
        etUsername.isEnabled = !show
        etPassword.isEnabled = !show
    }

    companion object {
        const val PREFERENCE_USERNAME = "recentUsername"
        const val PREFERENCE_PASSWORD = "recentPassword"
    }
}