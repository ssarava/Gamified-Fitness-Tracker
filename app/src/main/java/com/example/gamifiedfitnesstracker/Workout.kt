package com.example.gamifiedfitnesstracker

enum class Workout(val displayName: String) {
    BENCH_PRESS("Bench Press"),
    CURL("Curl"),
    CUSTOM("Custom"),
    DEFAULT("Default"),
    NONE(""),
    PUSH_UP("Push Up"),
    RUN("Run"),
    SQUAT("Squat");

    fun isUnimplemented() = this == CUSTOM || this == DEFAULT || this == NONE
}