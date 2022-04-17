package io.invest.app.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.robinhood.spark.SparkAdapter
import com.robinhood.ticker.TickerUtils
import dagger.hilt.android.AndroidEntryPoint
import io.invest.app.databinding.FragmentPortfolioBinding
import io.invest.app.util.PortfolioHistory
import io.invest.app.view.viewmodel.PortfolioViewModel
import java.math.BigDecimal
import java.math.RoundingMode

private const val TAG = "Portfolio"

@AndroidEntryPoint
class PortfolioFragment : Fragment() {
    private var _binding: FragmentPortfolioBinding? = null
    private val binding get() = _binding!!

    private val portfolioViewModel: PortfolioViewModel by viewModels()
    private var investing = BigDecimal(0)
    private var history = mutableListOf<PortfolioHistory>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPortfolioBinding.inflate(inflater, container, false)

        binding.investingTicker.setCharacterLists(TickerUtils.provideNumberList())

        portfolioViewModel.portfolio.observe(viewLifecycleOwner) {
            investing = it.value.toBigDecimal()
            binding.investingTicker.text = "\$${
                it.value.toBigDecimal().minus(it.cash.toBigDecimal())
                    .setScale(2, RoundingMode.HALF_UP).toPlainString()
            }"

            portfolioViewModel.getPortfolioHistory()
            Log.d(TAG, it.toString())
        }

        portfolioViewModel.portfolioHistory.observe(viewLifecycleOwner) {
            history.clear()
            history.addAll(it)

            binding.sparkView.adapter = object : SparkAdapter() {
                override fun getCount(): Int = history.size

                override fun getItem(index: Int) = history[index].change.toBigDecimal()

                override fun getY(index: Int): Float {
                    val change = getItem(index)
                    if (index == 0) return investing.plus(change).toFloat()
                    return getY(index - 1).toBigDecimal().plus(change).toFloat()
                }
            }
        }

        portfolioViewModel.getPortfolio()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}