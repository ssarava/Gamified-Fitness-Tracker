package com.example.testerapplication

import android.os.CountDownTimer
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import org.w3c.dom.Text
import kotlin.div
import kotlin.text.toInt

class ExerciseTimer : CountDownTimer{
    private var lengthInMinutes: Int
    private var timeLeft : Long =  1L

    constructor(exerciseInMinutes : Int) :
            super(exerciseInMinutes * 60 * 1000L, 1000L) {
                this.lengthInMinutes = exerciseInMinutes
    }

    var setOnTickListener: ((millisLeft: Long) -> Unit)? = null
    var setOnFinishListener: (() -> Unit)? = null

    override fun onTick(millisUntilFinished: Long) {
        setOnTickListener?.invoke(millisUntilFinished)
    }

    override fun onFinish() {
        setOnFinishListener?.invoke()
    }

    fun getLengthInMinutes() : Int {
        return this.lengthInMinutes
    }

    fun getTimeLeft() : Long {
        return this.timeLeft
    }

}