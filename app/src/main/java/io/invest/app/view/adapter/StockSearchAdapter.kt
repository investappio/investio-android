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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class StockSearchAdapter(
    context: Context,
    val investio: Investio
) : ArrayAdapter<Stock>(context, 0), Filterable {

    private var results = listOf<Stock>()
    private val inflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return results.count()
    }

    override fun getItem(index: Int): Stock {
        return results[index]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = if (convertView == null) {
            ListItemStockSearchBinding.inflate(inflater, parent, false)
        } else {
            ListItemStockSearchBinding.bind(convertView)
        }

        val stock = getItem(position)
        binding.stockNameView.text = stock.name
        binding.stockSymbolView.text = stock.symbol.uppercase()
        return binding.root
    }

    override fun getFilter(): Filter {
        val filter = object : Filter() {
            override fun performFiltering(query: CharSequence?): FilterResults {
                val filterResults = FilterResults()

                runBlocking(Dispatchers.IO) {
                    (investio.searchStocks(query.toString())?.stocks?.take(10) ?: emptyList()).let {
                        filterResults.values = it
                        filterResults.count = it.size
                    }
                }

                return filterResults
            }

            override fun publishResults(query: CharSequence?, filterResults: FilterResults?) {
                if (filterResults != null && filterResults.count > 0) {
                    results = filterResults.values as List<Stock>
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }

        return filter
    }
}