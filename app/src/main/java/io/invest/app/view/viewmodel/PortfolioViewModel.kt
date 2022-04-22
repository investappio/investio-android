package io.invest.app.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import io.invest.app.net.Investio
import io.invest.app.util.Portfolio
import io.invest.app.util.PortfolioHistory
import io.invest.app.util.TimeRange
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import kotlin.time.Duration.Companion.days

@HiltViewModel
class PortfolioViewModel @Inject constructor(private val investio: Investio) : ViewModel() {
    private val _portfolio: MutableLiveData<Portfolio> = MutableLiveData()
    private val _portfolioHistory: MutableLiveData<List<PortfolioHistory>> = MutableLiveData()

    private val portfolio get() = _portfolio
    private val portfolioHistory get() = _portfolioHistory

    val portfolioFlow get() = combine(portfolio.asFlow(), portfolioHistory.asFlow()) { portfolio, history ->
        PortfolioModel(portfolio, history)
    }

    suspend fun getPortfolio() {
        investio.getPortfolio()?.portfolio?.let {
            _portfolio.value = it
        }
    }

    suspend fun getPortfolioHistory(timeRange: TimeRange = TimeRange.WEEKS) {
        val count = when (timeRange) {
            TimeRange.WEEKS -> 14
            TimeRange.MONTHS -> 30 * 3
            TimeRange.YEAR -> 365
        }

        investio.getPortfolioHistory(timeRange)?.history?.let { history ->
            _portfolioHistory.value = MutableList(count - history.size) {
                PortfolioHistory(
                    history.last().timestamp
                        .minus(1.days.times(count - it - 1)), 0f, 0f
                )
            }.plus(history)
        }
    }

    data class PortfolioModel(val portfolio: Portfolio, val history: List<PortfolioHistory>)
}