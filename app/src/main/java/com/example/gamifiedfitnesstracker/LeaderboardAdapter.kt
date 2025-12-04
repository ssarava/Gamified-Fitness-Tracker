package com.example.gamifiedfitnesstracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

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

    private fun statToString(stat: Int?, isRun: Boolean): String =
        if (isRun) "$stat mile${if (stat == 1) "" else "s"}"
        else "$stat rep${if (stat == 1) "" else "s"}"


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val leaderboard = LeaderboardActivity.leaderboard
        val player = leaderboard.getPlayers()[position]

        // Set rank
        (position + 1).toString().also { holder.tvRank.text = it }

        // Set username
        holder.tvUsername.text = player.username

        // Set metric value based on current sort mode
        holder.tvMetricValue.text =
            when (leaderboard.getCurrentSortMode()) {
                Workout.BENCH_PRESS -> statToString(player.bpBest, false)
                Workout.CURL -> statToString(player.curlBest, false)
                Workout.NONE -> ""
                Workout.PUSH_UP -> statToString(player.pushUpBest, false)
                Workout.RUN -> statToString(player.runBest, true)
                Workout.SQUAT -> statToString(player.squatBest, false)
            }

        // Highlight current user
        val isCurrUser = player.username == leaderboard.getCurrentUsername()
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

        if (isCurrUser) holder.tvUsername.text =
            holder.view.context.getString(R.string.tvUsername_text_you, player.username)

        // Special styling for top 3 ranks
        when (position + 1) {
            1 -> styleRank(holder, R.drawable.ic_gold_trophy, R.color.gold)
            2 -> styleRank(holder, R.drawable.ic_silver_trophy, R.color.silver)
            3 -> styleRank(holder, R.drawable.ic_bronze_trophy, R.color.bronze)
            else -> styleRank(holder, R.drawable.rank_badge_default, R.color.text_secondary)
        }
    }

    private fun styleRank(holder: ViewHolder, drawable: Int, color: Int) {
        holder.rankBadge.setBackgroundResource(drawable)
        holder.tvRank.setTextColor(
            ContextCompat.getColor(holder.itemView.context, color)
        )
    }

    override fun getItemCount() = LeaderboardActivity.leaderboard.getPlayers().size
}