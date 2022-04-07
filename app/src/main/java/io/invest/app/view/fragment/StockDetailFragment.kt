package io.invest.app.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import dagger.hilt.android.AndroidEntryPoint
import io.invest.app.databinding.FragmentStockDetailBinding
import io.invest.app.net.Investio
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "StockDetail"

@AndroidEntryPoint
class StockDetailFragment : Fragment() {
    private var _binding: FragmentStockDetailBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var investio: Investio

    private val args: StockDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStockDetailBinding.inflate(inflater)
        binding.textView.text = args.symbol

        binding.chart.apply {

            axisLeft.apply {
                setDrawGridLines(false)
            }

            axisRight.apply {
                setDrawGridLines(false)
            }

            xAxis.apply {
                setDrawGridLines(false)
                setDrawLabels(false)
                granularity = 1f
                isGranularityEnabled = true
                setAvoidFirstLastClipping(true)
            }

            isDragEnabled = false
            setScaleEnabled(false)
            isDoubleTapToZoomEnabled = false
            legend.isEnabled = false
        }

        lifecycleScope.launch {
            val res = investio.getPrices(args.symbol)

            res?.prices?.let {
                val entries = it.mapIndexed { index, price ->
                    Log.d(TAG, index.toString())
                    CandleEntry(
                        index.toFloat(),
                        price.high,
                        price.low,
                        price.open,
                        price.close
                    )
                }

                val dataSet = CandleDataSet(entries, "")

                binding.chart.data = CandleData(dataSet)
                binding.chart.invalidate()
                binding.chart.fitScreen()
            }

            Log.d(TAG, res?.prices.toString())
        }

        (activity as AppCompatActivity).supportActionBar?.title = args.symbol
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}