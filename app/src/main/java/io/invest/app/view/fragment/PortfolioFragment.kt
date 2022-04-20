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
import io.invest.app.util.format
import io.invest.app.util.formatLocal
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
    private val activity get() = requireActivity() as AppCompatActivity
    private val history = mutableListOf<PortfolioHistory>()

    private val yearDateFormat = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())

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

        portfolioViewModel.portfolio.observe(viewLifecycleOwner) {
            investing = it.value.toBigDecimal().minus(it.cash.toBigDecimal())
                .setScale(2, RoundingMode.HALF_UP)
            binding.investingTicker.text = "\$${investing.toPlainString()}"
            binding.collapsingToolbarLayout.title =
                "Cash: \$${it.cash.toBigDecimal().setScale(2, RoundingMode.HALF_UP)}"
        }

        portfolioViewModel.portfolioHistory.observe(viewLifecycleOwner) {
            history.clear()
            history.addAll(it)
            binding.sparkView.adapter.notifyDataSetChanged()

            binding.sparkView.lineColor = if (history.last().value < history.first().value) {
                MaterialColors.getColor(
                    binding.sparkView,
                    com.google.android.material.R.attr.colorError
                )
            } else {
                MaterialColors.getColor(binding.sparkView, R.attr.colorSuccess)
            }

            binding.historicalDate.text = Clock.System.now().formatLocal(yearDateFormat)
        }

        lifecycleScope.launchWhenCreated {
            portfolioViewModel.getPortfolio()
            portfolioViewModel.getPortfolioHistory()
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
                binding.historicalDate.text = history.timestamp.format(yearDateFormat)
                binding.investingTicker.text =
                    "\$${history.value.toBigDecimal().minus(history.cash.toBigDecimal())}"
                return@OnScrubListener
            }

            binding.historicalDate.text = Clock.System.now().formatLocal(yearDateFormat)
            binding.investingTicker.text = "\$${investing.toPlainString()}"
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