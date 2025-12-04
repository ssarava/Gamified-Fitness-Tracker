package com.example.gamifiedfitnesstracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale
import java.util.concurrent.TimeUnit

class LeaderboardAdapter : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvRank: TextView = view.findViewById(R.id.tvRank)
        val tvUsername: TextView = view.findViewById(R.id.tvUsername)
        val tvMetricValue: TextView = view.findViewById(R.id.tvMetricValue)
        val itemContainer: View = view.findViewById(R.id.itemContainer)
        val rankBadge: View = view.findViewById(R.id.rankBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val leaderboard = LeaderboardActivity.leaderboard
        val player = leaderboard.getPlayers()[position]

        // Set rank
//        holder.tvRank.text = player.rank.toString()
        (position + 1).toString().also { holder.tvRank.text = it }

        // Set username
        holder.tvUsername.text = player.username

        // Set metric value based on current sort mode
        holder.tvMetricValue.text =
            if (leaderboard.getCurrentSortMode() == Leaderboard.SortMode.CALORIES)
                "${player.caloriesBurned} kcal"
            else
                formatDuration(player.workoutDuration!!.toLong())

        // Highlight current user
        val isCurrUser = player.userId == leaderboard.getCurrentUserId()
        holder.itemContainer.setBackgroundColor(
            ContextCompat.getColor(
                holder.itemView.context,
                if (isCurrUser) R.color.current_user_highlight else android.R.color.transparent
            )
        )

        holder.tvUsername.setTextColor(
            ContextCompat.getColor(
                holder.itemView.context,
                if (isCurrUser) R.color.primary_color else R.color.text_primary
            )
        )

        if (isCurrUser)
            holder.tvUsername.text =
//                "${player.username} (You)"
                holder.view.context.getString(R.string.tvUsername_text_you, player.username)


        // Special styling for top 3 ranks
        when (position + 1) {
            1 -> {
                styleRank(holder, R.drawable.rank_badge_gold, R.color.gold)
            }

            2 -> {
                styleRank(holder, R.drawable.rank_badge_silver, R.color.silver)
            }

            3 -> {
                styleRank(holder, R.drawable.rank_badge_bronze, R.color.bronze)
            }

            else -> {
                styleRank(holder, R.drawable.rank_badge_default, R.color.text_secondary)
            }
        }
    }

    private fun styleRank(holder: ViewHolder, drawable: Int, color: Int) {
        holder.rankBadge.setBackgroundResource(drawable)
        holder.tvRank.setTextColor(
            ContextCompat.getColor(holder.itemView.context, color)
        )
    }

    override fun getItemCount(): Int {
        return LeaderboardActivity.leaderboard.getPlayers().size
    }

    /**
     * Format duration from milliseconds to readable string
     */
    private fun formatDuration(durationMillis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(durationMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60

        return when {
            hours > 0 ->
                String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
//                String.format("%02d:%02d:%02d", hours, minutes, seconds)
            else ->
//                String.format("%02d:%02d", minutes, seconds)
                String.format(Locale.US, "%02d:%02d", minutes, seconds)
        }
    }
}