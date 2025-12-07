package com.example.gamifiedfitnesstracker

import android.content.Context
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random

object Utilities {
    val USERS =
        FirebaseDatabase.getInstance().reference.child("users")     // singleton database reference
    const val PREFERENCE_USERNAME = "recentUsername"
    const val PREFERENCE_PASSWORD = "recentPassword"
    const val FAILED_USERNAME = "invalid_user"
    const val BEST_BENCH = "benchBest"
    const val BEST_CURL = "curlBest"
    const val BEST_PUSH_UP = "pushUpBest"
    const val BEST_RUN = "runBest"
    const val BEST_SQUAT = "squatBest"
    const val WORKOUT_NAME = "workout_name"
    const val WORKOUT_BEST = "workout_best"
    const val UNIT = "workout_unit"

    fun initializeToast(context: Context, text: String) =
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()

    fun createNewUser(user: String, pw: String, test: Boolean): HashMap<String, *> {
        val r = Random()
        val date = SimpleDateFormat("MM/dd/yyyy hh:mm:ss", Locale.US).format(Date())
        val userData = hashMapOf(
            "Username" to user,
            "Password" to hashPassword(pw),
            "Created On" to date,
            "Personal Bests" to hashMapOf(
                Workout.BENCH_PRESS.displayName to if (test) r.nextInt(101) else 0,
                Workout.CURL.displayName to if (test) r.nextInt(101) else 0,
                Workout.CUSTOM.displayName to HashMap<String, Any>(),
                Workout.PUSH_UP.displayName to if (test) r.nextInt(101) else 0,
                Workout.RUN.displayName to if (test) r.nextInt(101) else 0,
                Workout.SQUAT.displayName to if (test) r.nextInt(101) else 0
            )
        )
        return userData
    }

    // Hash password using SHA-256
    fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    fun clearTestData(all: Boolean = false, vararg usernames: String) {
        if (all) {
            USERS.removeValue()
            return
        }
        for (username in usernames) {
            USERS.child(username).removeValue()
        }
        USERS.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in 0..snapshot.childrenCount) {
                    USERS.child("random_user_$i").removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Database Error ${error.code}: ${error.message}")
            }

        })

    }

    fun populateTestData(numOfUsers: Int = 3) {
        for (i in 1..numOfUsers) {
            val username = "random_user_$i"
            val pw = "random_password_$i"
            val newUser = createNewUser(username, pw, true)

            USERS.child(username).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) USERS.child(username).setValue(newUser)
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Database Error ${error.code}: ${error.message}")
                }

            })


//                .setValue(newUser)
//                .addOnSuccessListener { println("Successfully added user $username") }
//                .addOnFailureListener { e -> println("Failed to create account: ${e.message}") }
        }
    }
}