package com.example.gamifiedfitnesstracker

data class Player(
    var username: String? = null,
    var bpBest: Int? = null,
    var curlBest: Int? = null,
    var pushUpBest: Int? = null,
    var runBest: Int? = null,
    var squatBest: Int? = null
) {
    override fun toString(): String = "name: $username\tbpBest: $bpBest\tcurlBest: " +
            "$curlBest\tpushUpBest: $pushUpBest\trunBest: $runBest\tsquatBest: $squatBest\t"

}

// for now: squats, push-ups, runs
enum class Workout(val displayName: String? = null) {
    BENCH_PRESS("Bench Press"),
    CURL("Curl"),
    PUSH_UP("Push Up"),
    RUN("Run"),
    SQUAT("Squat"),
    NONE
}