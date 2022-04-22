package io.invest.app.view.fragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.robinhood.spark.SparkView
import com.robinhood.ticker.TickerUtils
import dagger.hilt.android.AndroidEntryPoint
import io.invest.app.R
import io.invest.app.databinding.FragmentPortfolioBinding
import io.invest.app.util.*
import io.invest.app.view.adapter.AssetListAdapter
import io.invest.app.view.adapter.SparkAdapter
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
    private val portfolioAssets = mutableMapOf<String, Float>()
    private val history = mutableListOf<PortfolioHistory>()
    private val assetAdapter = AssetListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPortfolioBinding.inflate(inflater, container, false)

        setupChart()
        setHasOptionsMenu(true)

        activity.apply {
            setSupportActionBar(binding.toolbar)
            setupActionBarWithNavController(findNavController())
        }

        binding.investingTicker.setCharacterLists(TickerUtils.provideNumberList())
        binding.assetList.adapter = assetAdapter

        binding.swipeRefresh.setOnRefreshListener {
            refresh()
        }

        binding.assetToggle.setOnClickListener {
            binding.assetList.let {
                it.visibility = if (it.visibility == View.GONE) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    portfolioViewModel.portfolioFlow.collect { data ->
                        val portfolio = data.portfolio
                        val portfolioHistory = data.history

                        portfolioAssets.clear()
                        portfolioAssets.putAll(portfolio.assets)

                        history.clear()
                        history.addAll(portfolioHistory)
                        binding.sparkView.adapter.notifyDataSetChanged()

                        val cash =
                            history.last().cash.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
                        val value =
                            history.last().value.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
                        investing = value.minus(cash).setScale(2, RoundingMode.HALF_UP)

                        binding.investingTicker.text = "\$${investing}"
                        binding.collapsingToolbarLayout.title = "Cash: \$${cash}"

                        binding.historicalDate.text =
                            Clock.System.now().formatLocal(yearDateFormat(Locale.getDefault()))

                        assetViewModel.getAssets(*portfolio.assets.keys.toTypedArray())
                    }
                }

                launch {
                    assetViewModel.assetFlow.collect {
                        assetAdapter.assets = it
                        assetAdapter.portfolio = portfolioAssets
                        assetAdapter.itemList.clear()
                        assetAdapter.itemList.addAll(it.keys.toList())
                        assetAdapter.notifyItemRangeChanged(0, it.size)
                        binding.swipeRefresh.isRefreshing = false
                    }
                }

                refresh()
            }
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.portfolio, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_refresh -> refresh()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setupChart() {
        binding.sparkView.adapter =
            object : SparkAdapter<PortfolioHistory>(binding.sparkView, history) {
                override fun getValue(index: Int): Float {
                    val history = getItem(index)
                    return history.value - history.cash
                }
            }

        binding.sparkView.scrubListener = SparkView.OnScrubListener { history ->
            (history as PortfolioHistory?)?.let {
                binding.historicalDate.text =
                    history.timestamp.format(yearDateFormat(Locale.getDefault()))
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

    private fun refresh() {
        lifecycleScope.launch {
            portfolioViewModel.getPortfolio()
            portfolioViewModel.getPortfolioHistory()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}