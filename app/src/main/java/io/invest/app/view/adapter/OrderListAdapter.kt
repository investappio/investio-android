package io.invest.app.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.invest.app.databinding.ListItemOrderBinding
import io.invest.app.util.Order
import io.invest.app.util.format
import io.invest.app.util.yearDateFormat
import java.math.BigDecimal
import java.util.*

class OrderListAdapter(val itemList: MutableList<Order> = mutableListOf()) :
    RecyclerView.Adapter<OrderListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListAdapter.ViewHolder {
        val binding = ListItemOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = itemList[position]
        holder.bind(order)

    }

    override fun getItemCount(): Int {
        return itemList.size;
    }

    inner class ViewHolder(private val binding: ListItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.tvSymbol.text = order.symbol
            binding.tvNotional.text = "${if (order.side == "buy") "-" else "+"}\$${
                order.notional.toBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP)
            }"
            binding.tvSide.text = order.side.replaceFirstChar { it.uppercase() }
            binding.tvTimestamp.text = order.timestamp.format(yearDateFormat(Locale.getDefault()))
        }
    }


}