package io.invest.app.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import io.invest.app.net.Investio
import io.invest.app.util.Side
import io.invest.app.util.ValueType
import kotlinx.coroutines.flow.combine
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class TradeViewModel @Inject constructor(private val investio: Investio) : ViewModel() {
    private val whole = MutableLiveData("")
    private val decimal = MutableLiveData("")
    private val type = MutableLiveData(ValueType.NOTIONAL)
    private val side = MutableLiveData(Side.BUY)
    private val scale = MutableLiveData(2)
    private val success: MutableLiveData<Boolean> = MutableLiveData()

    private val isZero get() = (whole.value?.firstOrNull() == '0' || whole.value.isNullOrEmpty())
    private val inDecimal get() = decimal.value?.firstOrNull() == '.'

    val inputFlow
        get() = combine(whole.asFlow(), decimal.asFlow()) { whole, decimal ->
            whole.ifEmpty { "0" } + decimal
        }

    val typeFlow get() = type.asFlow()
    val sideFlow get() = side.asFlow()
    val successFlow get() = success.asFlow()

    fun input(char: Char) {
        if (inDecimal) {
            if (char == '.') return
            if (decimal.value?.length ?: 0 > scale.value ?: 0) return

            decimal.value = decimal.value?.plus(char)
            return
        }

        if (char == '0' && isZero) return

        if (char == '.') {
            if (isZero) whole.value = "0"
            decimal.value = "."
            return
        }

        whole.value = whole.value?.plus(char)
    }

    fun delete() {
        if (inDecimal) {
            decimal.value = decimal.value?.dropLast(1)

            if (!inDecimal && isZero) whole.value = whole.value?.trimStart('0')
            return
        }

        whole.value = whole.value?.dropLast(1)
    }

    fun setValue(str: String) {
        val parts = str.split(".")

        whole.value = parts[0]
        decimal.value = parts.getOrElse(1) { "" }
    }

    fun setType(t: ValueType, quote: BigDecimal) {
        if (t == type.value) return

        val value =
            BigDecimal(whole.value?.ifEmpty { "0" } + if (decimal.value == ".") ".00" else decimal.value)

        val res = if (t == ValueType.NOTIONAL) quote.times(value)
            .setScale(2, RoundingMode.HALF_UP) else value.divide(
            quote,
            6,
            RoundingMode.HALF_UP
        )

        type.value = t

        val parts = res.toPlainString().split('.')
        val dec = parts.getOrNull(1)?.trimEnd('0') ?: ""

        scale.value = if (t == ValueType.NOTIONAL) 2 else 6
        whole.value = parts.first().trimStart('0')
        decimal.value = if (dec == "") "" else ".$dec"
    }

    suspend fun trade(symbol: String) {
        val value = BigDecimal(whole.value?.ifEmpty { "0" } + decimal.value)
        val res = if (side.value == Side.BUY) investio.buyAsset(
            symbol,
            value.toFloat(),
            type.value ?: ValueType.NOTIONAL
        ) else investio.sellAsset(symbol, value.toFloat(), type.value ?: ValueType.NOTIONAL)

        success.value = res?.success == true
    }

    fun refresh() {
        type.value = type.value
        side.value = side.value
    }

    fun setSide(s: Side) {
        if (s == side.value) return
        side.value = s
    }
}