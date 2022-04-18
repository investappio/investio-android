package io.invest.app.view.fragment

import android.os.Bundle
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
import io.invest.app.R
import io.invest.app.databinding.FragmentPortfolioBinding
import io.invest.app.util.PortfolioHistory
import io.invest.app.util.TimeRange
import io.invest.app.util.format
import io.invest.app.view.viewmodel.PortfolioViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.format.DateTimeFormatter
import java.util.*

private const val TAG = "Portfolio"

@AndroidEntryPoint
class PortfolioFragment : Fragment() {
    private var _binding: FragmentPortfolioBinding? = null
    private val binding get() = _binding!!

    private val portfolioViewModel: PortfolioViewModel by viewModels()
    private var investing = BigDecimal(0)
    private val history = mutableListOf<PortfolioHistory>()

    private val yearDateFormat = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())

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
        }

        binding.sparkView.adapter = object : SparkAdapter() {
            override fun getCount(): Int = history.size

            override fun getItem(index: Int) = history.getOrNull(index)

            override fun getY(index: Int): Float {
                return getItem(index)?.value ?: 0f
            }
        }

        binding.sparkView.scrubListener = SparkView.OnScrubListener { history ->
            (history as PortfolioHistory?)?.let {
                binding.historicalDate.text = history.timestamp.format(yearDateFormat)
                binding.investingTicker.text = "\$${history.value}"
                return@OnScrubListener
            }

            binding.historicalDate.text = Clock.System.now().format(yearDateFormat)
            binding.investingTicker.text = "\$${investing.toPlainString()}"
        }

        portfolioViewModel.portfolioHistory.observe(viewLifecycleOwner) {
            history.clear()
            history.addAll(it)
            binding.sparkView.adapter.notifyDataSetChanged()
            binding.historicalDate.text = Clock.System.now().format(yearDateFormat)
        }

        binding.historyToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                lifecycleScope.launch {
                    portfolioViewModel.getPortfolioHistory(
                        when (checkedId) {
                            R.id.month_history ->
                                TimeRange.MONTHS
                            R.id.year_history ->
                                TimeRange.YEAR
                            else ->
                                TimeRange.WEEKS
                        }
                    )
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            portfolioViewModel.getPortfolio()
            portfolioViewModel.getPortfolioHistory(TimeRange.WEEKS)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}