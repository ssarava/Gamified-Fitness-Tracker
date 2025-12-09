package com.example.gamifiedfitnesstracker

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class EmailNotificationManager(private val context: Context) {

    fun checkAndNotifyLeaderboardChange(
        workout: Workout,
        currentUsername: String,
        newScore: Int,
        callback: (LeaderboardChangeResult) -> Unit
    ) {
        // Load all users and their personal bests
        Utilities.USERS.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val playerScores = mutableListOf<PlayerScore>()

                // Parse all players and their scores for this workout
                for (userSnapshot in snapshot.children) {
                    val username = userSnapshot.child("Username").getValue(String::class.java)
                    val email = userSnapshot.child("Email").getValue(String::class.java) ?: ""

                    val personalBestsSnapshot = userSnapshot.child("Personal Bests")
                    val score = personalBestsSnapshot.child(workout.displayName)
                        .getValue(Int::class.java) ?: 0

                    if (username != null && score > 0) {
                        playerScores.add(
                            PlayerScore(
                                username = username,
                                score = score,
                                email = email
                            )
                        )
                    }
                }

                // Sort by score descending
                playerScores.sortByDescending { it.score }

                // Check if current user is now #1
                if (playerScores.isNotEmpty() && playerScores[0].username == currentUsername) {
                    // Current user is #1
                    if (playerScores.size > 1) {
                        val formerLeader = playerScores[1]
                        callback(
                            LeaderboardChangeResult.NewLeader(
                                newLeader = PlayerScore(currentUsername, newScore, ""),
                                formerLeader = formerLeader,
                                workoutType = workout
                            )
                        )
                    } else {
                        callback(LeaderboardChangeResult.FirstEntry)
                    }
                } else {
                    callback(LeaderboardChangeResult.NoChange)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(LeaderboardChangeResult.Error(error.message))
            }
        })
    }

    /**
     * Send congratulations email to new leader (yourself)
     */
    fun sendCongratulationsEmail(
        recipientEmail: String,
        recipientName: String,
        workout: Workout,
        newScore: Int
    ) {
        val subject = "🎉 You're #1 in ${workout.displayName}!"
        val body = """
            Hi $recipientName,
            
            Congratulations! You've just taken the #1 spot in ${workout.displayName} with a score of $newScore!
            
            Keep up the amazing work and maintain your position at the top!
            
            Best regards,
            Gamified Fitness Tracker Team
        """.trimIndent()

        sendEmail(recipientEmail, subject, body)
    }

    /**
     * Send notification email to former leader (person you overtook)
     */
    fun sendOvertakenEmail(
        recipientEmail: String,
        recipientName: String,
        newLeaderName: String,
        workout: Workout,
        theirScore: Int,
        newLeaderScore: Int
    ) {
        val subject = "⚠️ Your #1 spot in ${workout.displayName} has been taken!"
        val body = """
            Hi $recipientName,
            
            $newLeaderName has just overtaken your #1 position in ${workout.displayName}!
            
            Previous Leader: You ($theirScore)
            New Leader: $newLeaderName ($newLeaderScore)
            
            Time to hit the gym and reclaim your throne! 💪
            
            Best regards,
            Gamified Fitness Tracker Team
        """.trimIndent()

        sendEmail(recipientEmail, subject, body)
    }

    /**
     * Send bragging rights email to the person you overtook
     */
    fun sendBraggingRightsEmail(
        recipientEmail: String,
        recipientName: String,
        senderName: String,
        workout: Workout,
        senderScore: Int,
        recipientScore: Int
    ) {
        val subject = "👊 $senderName just overtook you in ${workout.displayName}!"
        val body = """
            Hi $recipientName,
            
            $senderName just crushed it and overtook your position in ${workout.displayName}!
            
            $senderName's score: $senderScore
            Your score: $recipientScore
            
            Think you can beat them? Time to prove it! 💪
            
            Challenge accepted?
            Gamified Fitness Tracker Team
        """.trimIndent()

        sendEmail(recipientEmail, subject, body)
    }
    private fun sendEmail(recipientEmail: String, subject: String, body: String) {
        if (recipientEmail.isEmpty()) {
            Utilities.initializeToast(context, "Recipient email not available")
            return
        }

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        try {
            context.startActivity(Intent.createChooser(intent, "Send Email"))
        } catch (e: Exception) {
            Utilities.initializeToast(context, "No email app found")
        }
    }
}

data class PlayerScore(
    val username: String,
    val score: Int,
    val email: String
)

sealed class LeaderboardChangeResult {
    data class NewLeader(
        val newLeader: PlayerScore,
        val formerLeader: PlayerScore,
        val workoutType: Workout
    ) : LeaderboardChangeResult()

    object FirstEntry : LeaderboardChangeResult()
    object NoChange : LeaderboardChangeResult()
    data class Error(val message: String) : LeaderboardChangeResult()
}