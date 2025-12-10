package com.example.gamifiedfitnesstracker

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
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
class SignInActivity : AppCompatActivity() {

    private lateinit var etUsername: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var usernameLayout: TextInputLayout
    private lateinit var emailLayout: TextInputLayout
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

        etEmail = findViewById(R.id.etEmail)
        etEmail.setText(sp.getString(Utilities.PREFERENCE_EMAIL, ""))

        etPassword = findViewById(R.id.etPassword)
        etPassword.setText(sp.getString(Utilities.PREFERENCE_PASSWORD, ""))

        usernameLayout = findViewById(R.id.usernameLayout)
        emailLayout = findViewById(R.id.emailLayout)
        passwordLayout = findViewById(R.id.passwordLayout)

        btnLogin = findViewById(R.id.btnLogin)
        btnLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_color))
        btnLogin.setOnClickListener { if (validCredentials()) loginUser() }

        btnSignUp = findViewById(R.id.btnSignUp)
        btnSignUp.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_color))
        btnSignUp.setOnClickListener { if (validCredentials(true)) signUpUser() }

        progressBar = findViewById(R.id.progressBar)
    }

    private fun isEmailValid(email: String): Boolean {
        return !email.isEmpty() && email.matches(Regex(Patterns.EMAIL_ADDRESS.pattern()))
    }

    private fun validCredentials(signingUp: Boolean = false): Boolean {
        var isValid = true
        val username = etUsername.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Clear previous errors
        usernameLayout.error = null
        emailLayout.error = null
        passwordLayout.error = null

        // Validate username
        if (username.isEmpty() || username.length < 3) {
            usernameLayout.error = "Username is required and must contain at least 3 characters"
            isValid = false
        } else if (!username.matches(Regex("^[a-zA-Z0-9_]+$"))) {
            usernameLayout.error = "Username may only contain letters, numbers, and underscores"
            isValid = false
        }

        // Validate email
        if (signingUp && !isEmailValid(email)) {
            emailLayout.error = "Valid email is required to sign up"
            Utilities.initializeToast(this, "Please input a valid email")
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

                // User exists - check hashed password input to stored password
                if (snapshot.exists()) {
                    val storedPassword = snapshot.child("Password").getValue(String::class.java)

                    // Password correct - successful login
                    if (storedPassword == Utilities.hashPassword(password)) {
                        showLoadingIndicator(false)
                        Utilities.initializeToast(
                            this@SignInActivity, "Login successful! Welcome back, $username"
                        )

                        // Save login and go to dashboard
                        saveLoginState(username, password)
                        goToNextScreen(DashboardActivity::class.java)
                    }

                    // Password incorrect - go to failed login screen
                    else {
                        showLoadingIndicator(false)
                        goToNextScreen(FailedLoginActivity::class.java)
                    }
                }

                // User doesn't exist - display error
                else {
                    showLoadingIndicator(false)
                    usernameLayout.error = "Username not found. Please sign up first."
                    Utilities.initializeToast(
                        this@SignInActivity, "Username not found. Please sign up first."
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showLoadingIndicator(false)
                Utilities.initializeToast(this@SignInActivity, "Error: ${error.message}")
            }
        })
    }

    private fun signUpUser() {
        val username = etUsername.text.toString().trim()    // Get username
        val email = etEmail.text.toString().trim()          // Get email
        val password = etPassword.text.toString().trim()    // Get password
        showLoadingIndicator(true)

//        // Validate email
//        if (isEmailValid(email)) {
//            emailLayout.error = "Valid email required for sign up"
//            Utilities.initializeToast(
//                this@SignInActivity,
//                "You must enter a valid email to sign up"
//            )
//            return
//        }


        // Check if username already exists
        val usersRef = Utilities.USERS.child(username)

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Username already taken
                    showLoadingIndicator(false)
                    usernameLayout.error = "Username already taken"
                    Utilities.initializeToast(
                        this@SignInActivity,
                        "Username exists. Please log in or choose a different username"
                    )

                } else {
                    // Username available, create new user
                    putUserInDatabase(username, email, password)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showLoadingIndicator(false)
                Utilities.initializeToast(this@SignInActivity, "Error: ${error.message}")
            }
        })
    }

    fun putUserInDatabase(username: String, email: String, password: String) {
        val newUser = Utilities.createNewUser(username, email, password)

        Utilities.USERS.child(username).setValue(newUser).addOnSuccessListener {
            showLoadingIndicator(false)
            Utilities.initializeToast(
                this@SignInActivity, "Account created successfully! Welcome, $username"
            )

            // Save login state
            saveLoginState(username, password)

            // Navigate to the next activity
            goToNextScreen(DashboardActivity::class.java)
        }.addOnFailureListener { e ->
            showLoadingIndicator(false)
            Utilities.initializeToast(
                this@SignInActivity, "Failed to create account: ${e.message}"
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
        if (activityClass == DashboardActivity::class.java) {
            finish()
        }
    }

    private fun showLoadingIndicator(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnLogin.isEnabled = !show
        btnSignUp.isEnabled = !show
        etUsername.isEnabled = !show
        etEmail.isEnabled = !show
        etPassword.isEnabled = !show
    }
}