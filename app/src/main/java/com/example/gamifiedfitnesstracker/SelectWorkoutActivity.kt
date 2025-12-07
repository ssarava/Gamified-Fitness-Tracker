package com.example.gamifiedfitnesstracker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.ImageButton

class SelectWorkoutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_workout)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<LinearLayout>(R.id.btnSquat).setOnClickListener {
            sendWorkout(Workout.SQUAT)
        }
        findViewById<LinearLayout>(R.id.btnPushUp).setOnClickListener {
            sendWorkout(Workout.PUSH_UP)
        }
        findViewById<LinearLayout>(R.id.btnRun).setOnClickListener {
            sendWorkout(Workout.RUN, "miles")
        }
        findViewById<LinearLayout>(R.id.btnBench).setOnClickListener {
            sendWorkout(Workout.BENCH_PRESS)
        }
        findViewById<LinearLayout>(R.id.btnCurl).setOnClickListener {
            sendWorkout(Workout.CURL)
        }
        findViewById<LinearLayout>(R.id.btnOther).setOnClickListener {
            openCustomWorkoutDialog()
        }
    }

    private fun sendWorkout(workout: Workout, measurement: String = "reps") {
        val sp = getSharedPreferences("${packageName}_preferences", MODE_PRIVATE)
        val personalBest =
            when (workout) {
                Workout.BENCH_PRESS -> intent.getStringExtra(Utilities.BEST_BENCH)!!
                Workout.CURL -> intent.getStringExtra(Utilities.BEST_CURL)!!
                Workout.CUSTOM -> sp.getString("NULL_CUSTOM", "NULL_CUSTOM")
                Workout.DEFAULT -> sp.getString("NULL_DEFAULT", "NULL_DEFAULT")
                Workout.NONE -> sp.getString("NULL_NONE", "NULL_NONE")
                Workout.PUSH_UP -> intent.getStringExtra(Utilities.BEST_PUSH_UP)!!
                Workout.RUN -> intent.getStringExtra(Utilities.BEST_RUN)!!
                Workout.SQUAT -> intent.getStringExtra(Utilities.BEST_SQUAT)!!
            }

        val intent = Intent(this, ExerciseLoggerActivity::class.java)
        intent.putExtra(Utilities.WORKOUT_NAME, workout.name)
        intent.putExtra(Utilities.WORKOUT_BEST, personalBest)
        intent.putExtra(Utilities.UNIT, measurement)
        startActivity(intent)
    }

    private fun openCustomWorkoutDialog() {

        // Use the updated dialog XML
        val dialogLayout = layoutInflater.inflate(R.layout.custom_workout, null)

        val nameInput: EditText = dialogLayout.findViewById(R.id.customWorkoutName)
        val measurementInput: EditText = dialogLayout.findViewById(R.id.customMeasurement)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Enter Custom Exercise")
            .setView(dialogLayout)
            .setPositiveButton("Start") { dialog, which ->
                val name = nameInput.text.toString().trim()
                val measurement = measurementInput.text.toString().trim()

                if (name.isEmpty()) nameInput.error = "Invalid Workout Name"
                if (measurement.isEmpty()) measurementInput.error = "Invalid measurement type"

                val workout = Workout.CUSTOM
                dialog.dismiss()
                sendWorkout(workout, measurement)

            }.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()
        dialog.show()

        val startBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val regex = Regex("^[a-zA-Z_]+$")
        startBtn.isEnabled = false

        nameInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                startBtn.isEnabled =
                    !s.isNullOrEmpty() && s.matches(regex) && measurementInput.text.matches(regex)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty() || !s.matches(regex)) {
                    nameInput.error = "Workout name must contain only letters"
                    startBtn.isEnabled = false
                }
            }
        })

        measurementInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                startBtn.isEnabled =
                    nameInput.text.matches(regex) && !s.isNullOrEmpty() && s.matches(regex)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty() || !s.matches(regex)) {
                    measurementInput.error = "Unit must contain only letters"
                    startBtn.isEnabled = false
                }
            }
        })
    }
}
