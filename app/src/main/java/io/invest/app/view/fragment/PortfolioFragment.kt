package io.invest.app.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.color.MaterialColors
import com.robinhood.spark.SparkAdapter
import com.robinhood.spark.SparkView
import com.robinhood.ticker.TickerUtils
import dagger.hilt.android.AndroidEntryPoint
import io.invest.app.R
import io.invest.app.databinding.FragmentPortfolioBinding
import io.invest.app.util.PortfolioHistory
import io.invest.app.util.TimeRange
import io.invest.app.util.formatLocal
import io.invest.app.util.yearDateFormat
import io.invest.app.view.viewmodel.AssetViewModel
import io.invest.app.view.viewmodel.PortfolioViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

private const val TAG = "Portfolio"

@AndroidEntryPoint
class PortfolioFragment : Fragment() {
    private var _binding: FragmentPortfolioBinding? = null
    private val binding get() = _binding!!

    private val assetViewModel: AssetViewModel by viewModels()
    private val portfolioViewModel: PortfolioViewModel by viewModels()
    private var investing = BigDecimal(0)
    private val activity get() = requireActivity() as AppCompatActivity
    private val history = mutableListOf<PortfolioHistory>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPortfolioBinding.inflate(inflater, container, false)

        setupChart()

        activity.apply {
            setSupportActionBar(binding.toolbar)
            setupActionBarWithNavController(findNavController())
        }

        binding.investingTicker.setCharacterLists(TickerUtils.provideNumberList())

        lifecycleScope.launchWhenCreated {
            portfolioViewModel.getPortfolio()
            portfolioViewModel.getPortfolioHistory()

            portfolioViewModel.portfolioFlow.collect { data ->
                val portfolio = data.portfolio
                val portfolioHistory = data.history

                history.clear()
                history.addAll(portfolioHistory)
                binding.sparkView.adapter.notifyDataSetChanged()

                binding.sparkView.lineColor = if (history.last().value < history.first().value) {
                    MaterialColors.getColor(
                        binding.sparkView,
                        com.google.android.material.R.attr.colorError
                    )
                } else {
                    MaterialColors.getColor(binding.sparkView, R.attr.colorSuccess)
                }

                val cash = history.last().cash.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
                val value = history.last().value.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
                investing = value.minus(cash).setScale(2, RoundingMode.HALF_UP)

                binding.investingTicker.text = "\$${investing}"
                binding.collapsingToolbarLayout.title = "Cash: \$${cash}"

                binding.historicalDate.text =
                    Clock.System.now().formatLocal(yearDateFormat(Locale.getDefault()))

                assetViewModel.getAssets(*portfolio.assets.keys.toTypedArray())
            }
        }

        return binding.root
    }

    private fun setupChart() {
        binding.sparkView.adapter = object : SparkAdapter() {
            override fun getCount(): Int = history.size

            override fun getItem(index: Int) = history.getOrNull(index)

            override fun getY(index: Int): Float {
                val history = getItem(index)
                return history?.let { it.value - it.cash } ?: 0f
            }
        }

        binding.sparkView.scrubListener = SparkView.OnScrubListener { history ->
            (history as PortfolioHistory?)?.let {
                binding.historicalDate.text =
                    history.timestamp.formatLocal(yearDateFormat(Locale.getDefault()))
                binding.investingTicker.text =
                    "\$${
                        history.value.toBigDecimal().minus(history.cash.toBigDecimal())
                            .setScale(2, RoundingMode.HALF_UP)
                    }"
                return@OnScrubListener
            }

            binding.historicalDate.text =
                Clock.System.now().formatLocal(yearDateFormat(Locale.getDefault()))
            binding.investingTicker.text = "\$${investing}"
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}