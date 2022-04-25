package io.invest.app.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.invest.app.databinding.ListItemLeaderboardBinding
import io.invest.app.util.LeaderboardItem

class LeaderboardAdapter :
    PagingDataAdapter<LeaderboardItem, LeaderboardAdapter.ViewHolder>(LeaderboardComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListItemLeaderboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val history = getItem(position)

        history?.let {
            holder.bind(history, position)
        }
    }

    inner class ViewHolder(val binding: ListItemLeaderboardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LeaderboardItem, position: Int) {
            binding.rank.text = (position + 1).toString()
            binding.value.text = "\$${item.value}"
            binding.username.text = item.user.username
        }
    }

    object LeaderboardComparator : DiffUtil.ItemCallback<LeaderboardItem>() {
        override fun areItemsTheSame(
            oldItem: LeaderboardItem,
            newItem: LeaderboardItem
        ): Boolean {
            return oldItem.user == newItem.user
        }

        override fun areContentsTheSame(
            oldItem: LeaderboardItem,
            newItem: LeaderboardItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}