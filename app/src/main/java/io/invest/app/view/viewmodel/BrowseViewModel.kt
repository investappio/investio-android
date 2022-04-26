package io.invest.app.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import io.invest.app.net.Investio
import io.invest.app.util.Asset
import io.invest.app.util.AssetPrice
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class BrowseViewModel @Inject constructor(private val investio: Investio) : ViewModel() {
    private val movers = MutableLiveData<List<AssetPriceModel>>()

    val moversFlow = movers.asFlow()

    suspend fun getMovers(count: Int) {
        investio.movers(count)?.let { res ->
            val symbols = res.assets.map { it.symbol }
            val quotes = investio.getQuotes(*symbols.toTypedArray()) ?: return
            val assets = symbols.mapNotNull { symbol ->
                investio.getAsset(symbol)?.asset?.let { symbol to it }
            }.toMap()

            movers.value = res.assets.map {
                val asset = assets[it.symbol] ?: return
                val quote = quotes.quotes[it.symbol] ?: return
                AssetPriceModel(asset, it, quote.toBigDecimal())
            }
        }
    }
}

data class AssetPriceModel(
    val asset: Asset,
    val price: AssetPrice,
    val quote: BigDecimal
)