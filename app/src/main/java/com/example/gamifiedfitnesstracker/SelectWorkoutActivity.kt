package com.example.gamifiedfitnesstracker

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton

class SelectWorkoutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_workout)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<LinearLayout>(R.id.btnBench).setOnClickListener {
            sendWorkout(Workout.BENCH_PRESS)
        }
        findViewById<LinearLayout>(R.id.btnCurl).setOnClickListener {
            sendWorkout(Workout.CURL)
        }
        findViewById<LinearLayout>(R.id.btnPushUp).setOnClickListener {
            sendWorkout(Workout.PUSH_UP)
        }
        findViewById<LinearLayout>(R.id.btnRun).setOnClickListener {
            sendWorkout(Workout.RUN, "miles")
        }
        findViewById<LinearLayout>(R.id.btnSquat).setOnClickListener {
            sendWorkout(Workout.SQUAT)
        }
        findViewById<LinearLayout>(R.id.btnSwim).setOnClickListener {
            sendWorkout(Workout.SWIM, "laps")
        }
    }

    private fun sendWorkout(workout: Workout, measurement: String = "reps") {
        val personalBest =
            when (workout) {
                Workout.BENCH_PRESS -> intent.getStringExtra(Utilities.BEST_BENCH)!!
                Workout.CURL -> intent.getStringExtra(Utilities.BEST_CURL)!!
                Workout.NONE -> ""
                Workout.PUSH_UP -> intent.getStringExtra(Utilities.BEST_PUSH_UP)!!
                Workout.RUN -> intent.getStringExtra(Utilities.BEST_RUN)!!
                Workout.SQUAT -> intent.getStringExtra(Utilities.BEST_SQUAT)!!
                Workout.SWIM -> intent.getStringExtra(Utilities.BEST_SWIM)!!
            }

        val intent = Intent(this, ExerciseLoggerActivity::class.java)
        intent.putExtra(Utilities.WORKOUT_NAME, workout.name)
        intent.putExtra(Utilities.WORKOUT_BEST, personalBest)
        intent.putExtra(Utilities.UNIT, measurement)
        startActivity(intent)
    }
}
