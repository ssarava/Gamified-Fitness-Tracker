package com.example.testerapplication
data class Player(
    var userId: String? = null,
    var username: String? = null,
    var caloriesBurned: Int? = null,
    var workoutDuration: Int? = null,   // in milliseconds
    var workoutRecords: HashMap<Workout, Number?> = hashMapOf(
//            Workout.SQUAT to null, Workout.PUSH_UP to null,
            Workout.RUN to null
        )
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        val temp = other as Player
        return caloriesBurned == temp.caloriesBurned &&
                workoutDuration == temp.workoutDuration &&
                userId == temp.userId &&
                username == temp.username &&
                workoutRecords == other.workoutRecords
    }

    override fun hashCode(): Int {
        var result = caloriesBurned ?: 0
        result = 31 * result + (workoutDuration ?: 0)
        result = 31 * result + (userId?.hashCode() ?: 0)
        result = 31 * result + (username?.hashCode() ?: 0)
        result = 31 * result + workoutRecords.keys.toTypedArray().contentHashCode()
        return result
    }
}