package com.example.gamifiedfitnesstracker

import android.os.CountDownTimer

class ExerciseTimer : CountDownTimer {
    private var lengthInMinutes: Int
    private var timeLeft: Long = 1L

    constructor(minutes: Int) : super(minutes * 60 * 1000L, 1000L) {
        lengthInMinutes = minutes
    }

    var setOnTickListener: ((millisLeft: Long) -> Unit)? = null
    var setOnFinishListener: (() -> Unit)? = null

    override fun onTick(millisUntilFinished: Long) {
        setOnTickListener?.invoke(millisUntilFinished)
    }

    override fun onFinish() {
        setOnFinishListener?.invoke()
    }

    fun getLengthInMinutes() = lengthInMinutes

    fun getTimeLeft() = timeLeft
}