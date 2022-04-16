package io.invest.app.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.robinhood.ticker.TickerUtils
import dagger.hilt.android.AndroidEntryPoint
import io.invest.app.databinding.FragmentPortfolioBinding
import io.invest.app.view.viewmodel.PortfolioViewModel
import java.math.BigDecimal
import java.math.RoundingMode

private const val TAG = "Portfolio"

@AndroidEntryPoint
class PortfolioFragment : Fragment() {
    private var _binding: FragmentPortfolioBinding? = null
    private val binding get() = _binding!!

    private val portfolioViewModel: PortfolioViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPortfolioBinding.inflate(inflater, container, false)

        binding.investingTicker.setCharacterLists(TickerUtils.provideNumberList())

        portfolioViewModel.portfolio.observe(viewLifecycleOwner) {
            binding.investingTicker.text = "\$${
                BigDecimal(it.value.toDouble()).minus(BigDecimal(it.cash.toDouble()))
                    .setScale(2, RoundingMode.HALF_UP).toPlainString()
            }"
            Log.d(TAG, it.toString())
        }

        portfolioViewModel.getPortfolio()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}