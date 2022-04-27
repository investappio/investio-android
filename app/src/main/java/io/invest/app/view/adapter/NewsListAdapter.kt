package io.invest.app.view.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.invest.app.databinding.ListItemNewsBinding
import io.invest.app.util.News
import io.invest.app.util.format
import io.invest.app.util.monthDateFormat
import io.invest.app.util.yearMonthFormat
import kotlinx.datetime.Clock
import kotlinx.datetime.toDateTimePeriod
import java.util.*

class NewsListAdapter : PagingDataAdapter<News, NewsListAdapter.ViewHolder>(NewsComparator) {

    override fun onBindViewHolder(holder: NewsListAdapter.ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewsListAdapter.ViewHolder {
        val binding =
            ListItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(val binding: ListItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: News) {
            binding.source.text = item.source
            binding.interval.text =
                Clock.System.now().minus(item.timestamp).toDateTimePeriod().let { period ->
                    when (true) {
                        (period.years > 0) -> item.timestamp.format(yearMonthFormat(Locale.getDefault()))
                        (period.months > 0) -> item.timestamp.format(monthDateFormat(Locale.getDefault()))
                        (period.days > 0) -> "${period.days}d"
                        (period.hours > 0) -> "${period.hours}h"
                        (period.minutes > 0) -> "${period.minutes}m"
                        (period.seconds > 0) -> "${period.seconds}s"
                        else -> "1s"
                    }
                }
            binding.headline.text = item.headline

            binding.root.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
                binding.root.context.startActivity(intent)
            }
        }
    }

    object NewsComparator : DiffUtil.ItemCallback<News>() {
        override fun areItemsTheSame(
            oldItem: News,
            newItem: News
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: News,
            newItem: News
        ): Boolean {
            return oldItem == newItem
        }
    }
}