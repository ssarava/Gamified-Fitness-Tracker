package com.example.gamifiedfitnesstracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
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

    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var usernameLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var btnLogin: MaterialButton
    private lateinit var btnSignUp: MaterialButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeViews()       // Initialize UI components
//        Utilities.populateTestData(2)
    }

    /**
     * Initializes all UI components, sets listeners, and
     * auto-populates username and password if they're stored locally
     */
    private fun initializeViews() {
        val sp = getSharedPreferences("${packageName}_preferences", MODE_PRIVATE)

        etUsername = findViewById(R.id.etUsername)
        etUsername.setText(sp.getString(Utilities.PREFERENCE_USERNAME, ""))

        etPassword = findViewById(R.id.etPassword)
        etPassword.setText(sp.getString(Utilities.PREFERENCE_PASSWORD, ""))

        usernameLayout = findViewById(R.id.usernameLayout)
        passwordLayout = findViewById(R.id.passwordLayout)

        btnLogin = findViewById(R.id.btnLogin)
        btnLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_color))
        btnLogin.setOnClickListener { if (validCredentials()) loginUser() }

        btnSignUp = findViewById(R.id.btnSignUp)
        btnSignUp.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_color))
        btnSignUp.setOnClickListener { if (validCredentials()) signUpUser() }

        progressBar = findViewById(R.id.progressBar)
    }

    private fun validCredentials(): Boolean {
        var isValid = true
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Clear previous errors
        usernameLayout.error = null
        passwordLayout.error = null

        // Validate username
        if (username.isEmpty() || username.length < 3) {
            usernameLayout.error = "Username is required and must contain at least 3 characters"
            isValid = false
        } else if (!username.matches(Regex("^[a-zA-Z0-9_]+$"))) {
            usernameLayout.error = "Username may only contain letters, numbers, and underscores"
            isValid = false
        }

        // Validate password
        if (password.isEmpty() || password.length < 6) {
            passwordLayout.error = "Password is required and must contain at least 6 characters"
            isValid = false
        }

        return isValid
    }

    private fun loginUser() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        showLoadingIndicator(true)

        // Reference to users in Firebase
        val usersRef = Utilities.USERS.child(username)

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // User exists, check password
                    val storedPassword = snapshot.child("Password").getValue(String::class.java)

                    // Password correct - successful login
                    if (storedPassword == password) {
                        showLoadingIndicator(false)
                        Utilities.initializeToast(
                            this@MainActivity, "Login successful! Welcome back, $username"
                        )

                        // Save login state
                        saveLoginState(username, password)

                        // Navigate to the next activity
                        goToNextScreen(MainMenuActivity::class.java)
                    }

                    // Password incorrect - navigate to FailedLoginActivity
                    else {
                        showLoadingIndicator(false)
                        goToNextScreen(FailedLoginActivity::class.java)
                    }
                } else {
                    // User doesn't exist
                    showLoadingIndicator(false)
                    usernameLayout.error = "Username not found. Please sign up first."
                    Utilities.initializeToast(
                        this@MainActivity, "Username not found. Please sign up first."
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showLoadingIndicator(false)
                Utilities.initializeToast(this@MainActivity, "Error: ${error.message}")
            }
        })
    }

    private fun signUpUser() {
        val username = etUsername.text.toString().trim()    // Get username
        val password = etPassword.text.toString().trim()    // Get password
        showLoadingIndicator(true)

        // Check if username already exists
        val usersRef = Utilities.USERS.child(username)

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Username already taken
                    showLoadingIndicator(false)
                    usernameLayout.error = "Username already taken"
                    Utilities.initializeToast(
                        this@MainActivity,
                        "Username already exists. Please log in or choose a different username"
                    )

                } else {
                    // Username available, create new user
                    putUserInDatabase(username, password, true)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showLoadingIndicator(false)
                Utilities.initializeToast(this@MainActivity, "Error: ${error.message}")
            }
        })
    }

    fun putUserInDatabase(username: String, password: String, testUser: Boolean = false) {
        val newUser = Utilities.createNewUser(username, password, testUser)

        Utilities.USERS.child(username).setValue(newUser).addOnSuccessListener {
            showLoadingIndicator(false)
            Utilities.initializeToast(
                this@MainActivity, "Account created successfully! Welcome, $username"
            )

            // Save login state
            saveLoginState(username, password)

            // Navigate to the next activity
            goToNextScreen(MainMenuActivity::class.java)
        }.addOnFailureListener { e ->
            showLoadingIndicator(false)
            Utilities.initializeToast(
                this@MainActivity, "Failed to create account: ${e.message}"
            )
        }
    }

    private fun saveLoginState(username: String, password: String) {
        val sp = getSharedPreferences("${packageName}_preferences", MODE_PRIVATE)
        sp.edit(commit = true) {
            putString(Utilities.PREFERENCE_USERNAME, username)
            putString(Utilities.PREFERENCE_PASSWORD, password)
        }
    }

    private fun goToNextScreen(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        if (activityClass == FailedLoginActivity::class.java) {
            intent.putExtra(Utilities.FAILED_USERNAME, etUsername.text.toString().trim())
        }
        startActivity(intent)
        if (activityClass == MainMenuActivity::class.java) {
            finish()
        }
    }

    private fun showLoadingIndicator(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnLogin.isEnabled = !show
        btnSignUp.isEnabled = !show
        etUsername.isEnabled = !show
        etPassword.isEnabled = !show
    }
}