package com.example.gamifiedfitnesstracker

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.Toast
import com.example.gamifiedfitnesstracker.Constants.INTENT_STRING
import com.example.gamifiedfitnesstracker.Constants.LEADERBOARD_INTENT_STRING

class SelectWorkoutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_workout)

        findViewById<LinearLayout>(R.id.btnSquat).setOnClickListener {
            sendWorkout(Workout.SQUAT, "reps")
        }
        findViewById<LinearLayout>(R.id.btnPushUp).setOnClickListener {
            sendWorkout(Workout.PUSH_UP, "reps")
        }
        findViewById<LinearLayout>(R.id.btnRun).setOnClickListener {
            sendWorkout(Workout.RUN, "miles")
        }
        findViewById<LinearLayout>(R.id.btnBench).setOnClickListener {
            sendWorkout(Workout.BENCH_PRESS, "reps")
        }
        findViewById<LinearLayout>(R.id.btnCurl).setOnClickListener {
            sendWorkout(Workout.CURL, "reps")
        }

        findViewById<LinearLayout>(R.id.btnOther).setOnClickListener {
            openCustomWorkoutDialog()
        }
    }


    private fun sendWorkout(workout: Workout, measurement: String) {
        val intent = Intent(this, ExerciseLoggerActivity::class.java)
        intent.putExtra("workout_name", workout.displayName)
        intent.putExtra("workout_enum", workout.name)
        intent.putExtra("measurement_type", measurement)
        startActivity(intent)

        finish() // Remove from stack
    }

    private fun openCustomWorkoutDialog() {

        // Use the updated dialog XML
        val dialogLayout = layoutInflater.inflate(R.layout.custom_workout, null)

        val nameInput = dialogLayout.findViewById<EditText>(R.id.customWorkoutName)
        val measurementInput = dialogLayout.findViewById<EditText>(R.id.customMeasurement)

        AlertDialog.Builder(this)
            .setTitle("Enter Custom Exercise")
            .setView(dialogLayout)
            .setPositiveButton("Start") { _, _ ->
                val name = nameInput.text.toString().trim()
                val measure = measurementInput.text.toString().trim()

                if (name.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Please enter a workout name",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                if (measure.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Please enter a measurement type",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                val intent = Intent(this, ExerciseLoggerActivity::class.java)
                intent.putExtra("workout_name", name)
                intent.putExtra("workout_enum", "CUSTOM")
                intent.putExtra("measurement_type", measure)
                startActivity(intent)

                finish() // Remove from stack

            }
            .setNegativeButton("Cancel", null)
            .show()



    }
}
