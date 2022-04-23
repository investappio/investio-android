package io.invest.app.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import io.invest.app.net.Investio
import io.invest.app.util.Asset
import io.invest.app.util.AssetPrice
import kotlinx.coroutines.flow.combine
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class AssetViewModel @Inject constructor(private val investio: Investio) : ViewModel() {
    private val assets =  MutableLiveData<Map<String, Asset>>()
    private val priceHistories = MutableLiveData<Map<String, List<AssetPrice>>>()
    private val quotes = MutableLiveData<Map<String, BigDecimal>>()

    val assetFlow
        get() = combine(
            assets.asFlow(),
            priceHistories.asFlow(),
            quotes.asFlow()
        ) { assets, prices, quotes ->
            assets.keys.mapNotNull {
                val asset = assets[it] ?: return@mapNotNull null
                val priceHistory = prices[it] ?: return@mapNotNull null
                val quote = quotes[it] ?: return@mapNotNull null

                it to AssetModel(asset, priceHistory, quote)
            }.toMap()
        }

    suspend fun getAssets(vararg symbols: String) {
        val assets = symbols.mapNotNull { symbol ->
            investio.getAsset(symbol)?.asset?.let {
                symbol to it
            }
        }.toMap()

        val prices = symbols.mapNotNull { symbol ->
            investio.getPriceHistory(symbol)?.prices?.let {
                symbol to it
            }
        }.toMap()

        val quotes = investio.getQuotes(*symbols)?.quotes?.map {
            it.key to it.value.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
        }?.toMap() ?: emptyMap()

        this.assets.value = assets
        priceHistories.value = prices
        this.quotes.value = quotes
    }

    data class AssetModel(
        val asset: Asset,
        val priceHistory: List<AssetPrice>,
        val quote: BigDecimal
    )
}