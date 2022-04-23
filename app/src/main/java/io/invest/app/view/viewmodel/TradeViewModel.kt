package io.invest.app.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import io.invest.app.net.Investio
import kotlinx.coroutines.flow.combine
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class TradeViewModel @Inject constructor(private val investio: Investio) : ViewModel() {
    private val whole = MutableLiveData("")
    private val decimal = MutableLiveData("")
    private val scale = MutableLiveData(2)

    private val isZero get() = (whole.value?.firstOrNull() == '0' || whole.value.isNullOrEmpty())
    private val inDecimal get() = decimal.value?.firstOrNull() == '.'

    val inputFlow = combine(whole.asFlow(), decimal.asFlow()) { whole, decimal ->
        whole.ifEmpty { "0" } + decimal
    }

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
            return
        }

        whole.value = whole.value?.dropLast(1)
    }

    fun setValue(str: String) {
        val parts = str.split(".")

        whole.value = parts[0]
        decimal.value = parts.getOrElse(1) { "" }
    }

    fun setScale(n: Int) {
        if (decimal.value?.length ?: 0 > n  + 1) {
            val big = BigDecimal("0${decimal}").setScale(n, BigDecimal.ROUND_HALF_UP).toPlainString()
            decimal.value = big.split(".").last()
        }

        scale.value = n
    }
}