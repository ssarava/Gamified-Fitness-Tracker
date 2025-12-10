package com.example.gamifiedfitnesstracker

data class Player(
    var username: String? = null,
    var email: String? = null,  // Added email field for notifications
    var bpBest: Int? = null,
    var curlBest: Int? = null,
    var pushUpBest: Int? = null,
    var runBest: Int? = null,
    var squatBest: Int? = null,
    var swimBest: Int? = null
) {
    // Secondary constructor for backward compatibility
    constructor(
        username: String?,
        bpBest: Int?,
        curlBest: Int?,
        pushUpBest: Int?,
        runBest: Int?,
        squatBest: Int?,
        swimBest: Int?
    ) : this(username, null, bpBest, curlBest, pushUpBest, runBest, squatBest, swimBest)

    override fun toString(): String =
        "name: $username\temail: $email\tbpBest: $bpBest\tcurlBest: " +
                "$curlBest\tpushUpBest: $pushUpBest\trunBest: " +
                "$runBest\tsquatBest: $squatBest\tswimBest: $swimBest"
}