package io.invest.app.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import io.invest.app.databinding.ListItemStockSearchBinding
import io.invest.app.net.Investio
import io.invest.app.util.Stock
import kotlinx.coroutines.runBlocking

class StockSearchAdapter(
    context: Context,
    val investio: Investio
) : ArrayAdapter<Stock>(context, 0), Filterable {

    private var results = listOf<Stock>()
    private val inflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return results.size
    }

    override fun getItem(index: Int): Stock {
        return results[index]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: ViewHolder = if (convertView != null) {
            val stock = getItem(position)
            val holder = convertView.tag as ViewHolder
            holder.binding.stockNameView.text = stock.name
            holder.binding.stockSymbolView.text = stock.symbol
            holder
        } else {
            val binding = ListItemStockSearchBinding.inflate(inflater, parent, false)
            val holder = ViewHolder(binding)
            binding.root.tag = holder
            holder
        }

        return viewHolder.binding.root
    }

    override fun getFilter(): Filter {
        val filter = object : Filter() {
            override fun performFiltering(query: CharSequence?): FilterResults {
                val filterResults = FilterResults()

                runBlocking {
                    results =
                        investio.searchStocks(query.toString())?.stocks?.take(10) ?: emptyList()

                    filterResults.values = results
                    filterResults.count = results.size
                }

                return filterResults
            }

            override fun publishResults(query: CharSequence?, filterResults: FilterResults?) {
                if (results.isNotEmpty()) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }

        return filter
    }

    inner class ViewHolder(val binding: ListItemStockSearchBinding)
}