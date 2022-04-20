package io.invest.app.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.invest.app.net.Investio
import io.invest.app.util.Stock
import io.invest.app.util.StockPrice
import io.invest.app.util.TimeRange
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class StockViewModel @Inject constructor(private val investio: Investio) : ViewModel() {
    private val _stock: MutableLiveData<Stock> = MutableLiveData()
    private val _priceHistory: MutableLiveData<List<StockPrice>> = MutableLiveData()
    private val _quote: MutableLiveData<BigDecimal> = MutableLiveData()

    val stock get() = _stock
    val priceHistory get() = _priceHistory
    val quote get() = _quote

    suspend fun getStock(symbol: String) {
        investio.getStock(symbol)?.stock?.let {
            _stock.value = it
        }
    }

    suspend fun getPriceHistory(symbol: String, timeRange: TimeRange = TimeRange.WEEKS) {
        investio.getPriceHistory(symbol, timeRange)?.prices?.let {
            _priceHistory.value = it
        }
    }

    suspend fun getQuote(symbol: String) {
        investio.getQuote(symbol)?.quote?.let {
            _quote.value = it.toBigDecimal()
        }
    }
}