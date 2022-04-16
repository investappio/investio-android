package io.invest.app.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.invest.app.net.Investio
import io.invest.app.util.Portfolio
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PortfolioViewModel @Inject constructor(private val investio: Investio) : ViewModel() {
    val portfolio: MutableLiveData<Portfolio> = MutableLiveData()

    init {
        getPortfolio()
    }

    fun getPortfolio() {
        viewModelScope.launch {
            investio.getPortfolio()?.portfolio?.let {
                portfolio.value = it
            }
        }
    }
}