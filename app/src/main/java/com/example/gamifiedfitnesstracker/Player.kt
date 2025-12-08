package com.example.gamifiedfitnesstracker

data class Player(
    var username: String? = null,
    var bpBest: Int? = null,
    var curlBest: Int? = null,
    var pushUpBest: Int? = null,
    var runBest: Int? = null,
    var squatBest: Int? = null,
    var swimBest: Int? = null
) {
    override fun toString(): String = "name: $username\tbpBest: $bpBest\tcurlBest: " +
            "$curlBest\tpushUpBest: $pushUpBest\trunBest: " +
            "$runBest\tsquatBest: $squatBest\tswimBest: $swimBest"
}