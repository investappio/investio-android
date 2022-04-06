package io.invest.app.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.invest.app.databinding.FragmentBrowseBinding
import io.invest.app.net.Investio
import io.invest.app.view.adapter.StockSearchAdapter
import javax.inject.Inject

private const val TAG = "Browse"

@AndroidEntryPoint
class BrowseFragment : Fragment() {
    private var _binding: FragmentBrowseBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var investio: Investio

    lateinit var stockSearchAdapter: StockSearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBrowseBinding.inflate(inflater, container, false)

        stockSearchAdapter = StockSearchAdapter(requireContext(), investio)
        binding.stockSearchInput.setAdapter(stockSearchAdapter)

        binding.stockSearchInput.setOnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            val stock = stockSearchAdapter.getItem(position)
            binding.stockSearchInput.setText("")

            val action =
                BrowseFragmentDirections.actionBrowseFragmentToStockDetailFragment(stock.symbol)
            findNavController().navigate(action)
            Log.d(TAG, stock.symbol)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}