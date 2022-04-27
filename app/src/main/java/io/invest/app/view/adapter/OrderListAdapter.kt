package io.invest.app.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.invest.app.databinding.ListItemAssetBinding
import io.invest.app.databinding.ListItemOrderBinding
import io.invest.app.util.Order
import io.invest.app.util.format
import io.invest.app.util.yearDateFormat
import java.util.*

class OrderListAdapter(val itemList: MutableList<Order> = mutableListOf()): RecyclerView.Adapter<OrderListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListAdapter.ViewHolder {
        val binding = ListItemOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
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
        RecyclerView.ViewHolder(binding.root){
            fun bind(order: Order)
            {
                binding.tvSymbol.text = order.symbol
                binding.tvQty.text = order.qty.toString()
                binding.tvNotional.text = order.notional.toString()
                if(order.side == "buy")
                {
                    binding.tvSide.text = "BUY"
                }
                else
                {
                    binding.tvSide.text = "SELL"
                }

                binding.tvTimestamp.text = order.timestamp.format(yearDateFormat(Locale.getDefault()))
            }
        }


}