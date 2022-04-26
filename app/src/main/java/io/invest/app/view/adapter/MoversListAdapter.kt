package io.invest.app.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.invest.app.databinding.ListItemAssetTileBinding
import io.invest.app.view.viewmodel.AssetPriceModel
import java.math.RoundingMode

class MoversListAdapter(val itemList: MutableList<AssetPriceModel>) :
    RecyclerView.Adapter<MoversListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemAssetTileBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = itemList.size

    inner class ViewHolder(val binding: ListItemAssetTileBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AssetPriceModel) {
            val percent = item.price.changePercent.toBigDecimal()
                .setScale(2, RoundingMode.HALF_UP)

            binding.symbol.text = item.asset.symbol
            binding.name.text = item.asset.name
            binding.changePercent.text =
                if (item.price.changePercent < 0) "$percent" else "+$percent"
        }
    }
}