package io.invest.app.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.invest.app.net.Investio
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "Investing"

@AndroidEntryPoint
class InvestingFragment : Fragment() {

    @Inject
    lateinit var investio: Investio

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lifecycleScope.launch {
            val res = investio.getPortfolio()
            Log.d(TAG, res?.toString() ?: "")
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}