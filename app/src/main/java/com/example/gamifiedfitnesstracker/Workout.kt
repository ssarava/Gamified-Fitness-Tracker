package com.example.gamifiedfitnesstracker

// for now: squats, push-ups, runs
enum class Workout(val displayName : String) {
    SQUAT("Squat"),
    PUSH_UP("Push Up"),
    BENCH_PRESS("Bench Press"),
    CURL("Curl"),
    RUN("Run"),
    DEFAULT("Exercise"),
    CUSTOM("CUSTOM"),
    NONE("")
}