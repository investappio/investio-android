package io.invest.app.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.robinhood.spark.SparkAdapter
import com.robinhood.spark.SparkView
import com.robinhood.ticker.TickerUtils
import dagger.hilt.android.AndroidEntryPoint
import io.invest.app.databinding.FragmentPortfolioBinding
import io.invest.app.util.PortfolioHistory
import io.invest.app.view.viewmodel.PortfolioViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

private const val TAG = "Portfolio"

@AndroidEntryPoint
class PortfolioFragment : Fragment() {
    private var _binding: FragmentPortfolioBinding? = null
    private val binding get() = _binding!!

    private val portfolioViewModel: PortfolioViewModel by viewModels()
    private var investing = BigDecimal(0)
    private val history = mutableListOf<PortfolioHistory>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPortfolioBinding.inflate(inflater, container, false)

        binding.investingTicker.setCharacterLists(TickerUtils.provideNumberList())

        portfolioViewModel.portfolio.observe(viewLifecycleOwner) {
            investing = it.value.toBigDecimal().minus(it.cash.toBigDecimal())
                .setScale(2, RoundingMode.HALF_UP)
            binding.investingTicker.text = "\$${investing.toPlainString()}"

            Log.d(TAG, it.toString())
        }

        portfolioViewModel.portfolioHistory.observe(viewLifecycleOwner) {
            history.clear()
            history.addAll(it)

            binding.sparkView.adapter = object : SparkAdapter() {
                override fun getCount(): Int = history.size

                override fun getItem(index: Int) = history[index]

                override fun getY(index: Int): Float {
                    return getItem(index).value
                }
            }

            binding.sparkView.scrubListener = SparkView.OnScrubListener { history ->
                (history as PortfolioHistory?)?.let {
                    binding.scrub.text = history.date
                    binding.investingTicker.text = "\$${history.value}"
                    return@OnScrubListener
                }

                binding.investingTicker.text = "\$${investing.toPlainString()}"
            }
        }

        lifecycleScope.launch {
            portfolioViewModel.getPortfolio()
            portfolioViewModel.getPortfolioHistory()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}