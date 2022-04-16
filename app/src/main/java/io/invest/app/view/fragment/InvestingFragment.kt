package io.invest.app.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import io.invest.app.databinding.FragmentInvestingBinding
import io.invest.app.view.viewmodel.PortfolioViewModel

private const val TAG = "Investing"

@AndroidEntryPoint
class InvestingFragment : Fragment() {
    private var _binding: FragmentInvestingBinding? = null
    private val binding get() = _binding!!

    private val portfolioViewModel: PortfolioViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInvestingBinding.inflate(inflater, container, false)

        portfolioViewModel.portfolio.observe(viewLifecycleOwner) {
            // TODO: Update UI with portfolio information
            Log.d(TAG, it.toString())
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}