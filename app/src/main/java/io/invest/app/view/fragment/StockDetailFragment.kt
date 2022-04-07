package io.invest.app.view.fragment

import android.graphics.Paint
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
import com.google.android.material.color.MaterialColors
import dagger.hilt.android.AndroidEntryPoint
import io.invest.app.R
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
                setDrawLabels(false)
                isEnabled = false
            }

            axisRight.apply {
                setDrawGridLines(false)
                setDrawLabels(false)
                isEnabled = false
            }

            xAxis.apply {
                setDrawGridLines(false)
                setDrawLabels(false)
                setAvoidFirstLastClipping(true)
            }

            setBorderColor(
                MaterialColors.getColor(
                    this,
                    com.google.android.material.R.attr.colorOnSurface
                )
            )

            setBackgroundColor(
                MaterialColors.getColor(
                    this,
                    com.google.android.material.R.attr.colorSurface
                )
            )

            isDragEnabled = false
            setScaleEnabled(false)
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false
            legend.isEnabled = false
        }

        lifecycleScope.launch {
            val res = investio.getPrices(args.symbol)

            res?.prices?.let {
                val entries = it.mapIndexed { index, price ->
                    CandleEntry(
                        index.toFloat(),
                        price.high,
                        price.low,
                        price.open,
                        price.close
                    )
                } as MutableList

//                if (entries.size < 5) {
//                    entries.addAll(
//                        (entries.size..4).map { index ->
//                            CandleEntry(index.toFloat(), 0f, 0f, 0f, 0f)
//                        })
//                }

                val dataSet = CandleDataSet(entries, "")

                dataSet.decreasingColor = MaterialColors.getColor(
                    binding.chart,
                    com.google.android.material.R.attr.colorError
                )

                dataSet.neutralColor = MaterialColors.getColor(
                    binding.chart,
                    com.google.android.material.R.attr.colorOnSurface
                )

                dataSet.increasingColor =
                    MaterialColors.getColor(binding.chart, R.attr.colorSuccess)

                dataSet.increasingPaintStyle = Paint.Style.FILL

                dataSet.shadowColor = dataSet.neutralColor

                binding.chart.data = CandleData(dataSet)
                binding.chart.fitScreen()
                binding.chart.moveViewToX(5f)
                binding.chart.invalidate()
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