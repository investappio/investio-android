package io.invest.app.view.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.invest.app.net.Investio
import javax.inject.Inject

@HiltViewModel
class PriceViewModel @Inject constructor(private val investio: Investio) : ViewModel() {

}