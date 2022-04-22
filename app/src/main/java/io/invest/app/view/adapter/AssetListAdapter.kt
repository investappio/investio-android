package io.invest.app.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.invest.app.databinding.ListItemAssetBinding
import io.invest.app.util.AssetPrice
import io.invest.app.view.viewmodel.AssetViewModel

class AssetListAdapter(val itemList: MutableList<String> = mutableListOf()) :
    RecyclerView.Adapter<AssetListAdapter.ViewHolder>() {
    var portfolio: Map<String, Float> = emptyMap()
    var assets: Map<String, AssetViewModel.AssetModel> = emptyMap()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemAssetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val symbol = itemList[position]

        if (portfolio.contains(symbol)) {
            holder.bind(assets[symbol], portfolio[symbol]!!)
        } else {
            holder.bind(assets[symbol])
        }
    }

    override fun getItemCount(): Int = itemList.size

    inner class ViewHolder(private val binding: ListItemAssetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: AssetViewModel.AssetModel?) {
            model?.let {
                binding.symbol.text = it.asset.symbol
                binding.price.text = "\$${it.quote}"
                binding.name.text = it.asset.name

                binding.sparkView.adapter =
                    object : SparkAdapter<AssetPrice>(binding.sparkView, it.priceHistory) {
                        override fun getValue(index: Int): Float = getItem(index).close
                    }
            }
        }

        fun bind(model: AssetViewModel.AssetModel?, count: Float) {
            bind(model)
            binding.quantity.visibility = View.VISIBLE
            binding.name.visibility = View.GONE
            binding.quantity.text = count.toString()
        }
    }
}