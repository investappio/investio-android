package io.invest.app.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.invest.app.net.Investio
import io.invest.app.util.Portfolio
import io.invest.app.util.PortfolioHistory
import javax.inject.Inject

@HiltViewModel
class PortfolioViewModel @Inject constructor(private val investio: Investio) : ViewModel() {
    private val _portfolio: MutableLiveData<Portfolio> = MutableLiveData()
    private val _portfolioHistory: MutableLiveData<List<PortfolioHistory>> = MutableLiveData()

    val portfolio get() = _portfolio
    val portfolioHistory get() = _portfolioHistory

    suspend fun getPortfolio() {
        investio.getPortfolio()?.portfolio?.let {
            _portfolio.postValue(it)
        }
    }

    suspend fun getPortfolioHistory() {
        investio.getPortfolioHistory()?.history?.let {
            _portfolioHistory.postValue(it)
        }
    }
}