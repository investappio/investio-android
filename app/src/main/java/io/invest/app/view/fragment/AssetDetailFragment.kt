package io.invest.app.view.fragment

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.google.android.material.color.MaterialColors
import com.robinhood.ticker.TickerUtils
import dagger.hilt.android.AndroidEntryPoint
import io.invest.app.R
import io.invest.app.databinding.FragmentAssetsDetailBinding
import io.invest.app.net.Investio
import io.invest.app.util.AssetPrice
import io.invest.app.view.viewmodel.AssetViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AssetDetail"

@AndroidEntryPoint
class AssetDetailFragment : Fragment() {
    private var _binding: FragmentAssetsDetailBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var investio: Investio

    private val args: AssetDetailFragmentArgs by navArgs()
    private val assetViewModel: AssetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAssetsDetailBinding.inflate(inflater)

        setupChart()
        binding.quote.setCharacterLists(TickerUtils.provideNumberList())

        binding.actionTrade.setOnClickListener {
            val action =
                AssetDetailFragmentDirections.actionAssetDetailFragmentToAssetTradeFragment(args.symbol)
            findNavController().navigate(action)
        }

        lifecycleScope.launch {
            assetViewModel.getAssets(args.symbol)

            assetViewModel.assetFlow.collect { data ->
                data[args.symbol]?.let {
                    binding.symbol.text = it.asset.symbol
                    binding.name.text = it.asset.name
                    binding.quote.text = "\$${it.quote}"
                    updateChart(it.priceHistory)
                }
            }
        }

        return binding.root
    }

    private fun updateChart(prices: List<AssetPrice>) {
        var min = Float.MAX_VALUE
        var max = Float.MIN_VALUE

        val entries = prices.mapIndexed { index, price ->
            max = maxOf(max, price.high, price.open, price.close)
            min = minOf(min, price.low, price.open, price.close)

            CandleEntry(
                index.toFloat(),
                price.high,
                price.low,
                price.open,
                price.close
            )
        }

        val dataSet = CandleDataSet(entries, "").apply {
            setDrawHorizontalHighlightIndicator(false)

            decreasingColor = MaterialColors.getColor(
                binding.chart,
                com.google.android.material.R.attr.colorError
            )

            increasingColor =
                MaterialColors.getColor(binding.chart, R.attr.colorSuccess)

            increasingPaintStyle = Paint.Style.FILL

            neutralColor = MaterialColors.getColor(
                binding.chart,
                com.google.android.material.R.attr.colorOnSurface
            )

            shadowColor = neutralColor

            setDrawValues(false)
        }

        binding.chart.data = CandleData(dataSet)
        binding.chart.xAxis.axisMaximum = maxOf(dataSet.entryCount.toFloat(), 10f)
        binding.chart.invalidate()
    }

    private fun setupChart() {
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
                setDrawAxisLine(false)
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
            isHighlightPerTapEnabled = false
            setDrawBorders(false)

            onChartGestureListener = object : OnChartGestureListener {
                override fun onChartGestureStart(
                    me: MotionEvent?,
                    lastPerformedGesture: ChartTouchListener.ChartGesture?
                ) {
                }

                override fun onChartGestureEnd(
                    me: MotionEvent?,
                    lastPerformedGesture: ChartTouchListener.ChartGesture?
                ) {
                }

                override fun onChartLongPressed(me: MotionEvent?) {}

                override fun onChartDoubleTapped(me: MotionEvent?) {}

                override fun onChartSingleTapped(me: MotionEvent?) {}

                override fun onChartFling(
                    me1: MotionEvent?,
                    me2: MotionEvent?,
                    velocityX: Float,
                    velocityY: Float
                ) {
                }

                override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {}

                override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}