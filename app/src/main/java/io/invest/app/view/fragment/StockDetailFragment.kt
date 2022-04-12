package io.invest.app.view.fragment

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
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

        setupChart()

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
                binding.chart.axisLeft.axisMinimum = dataSet.yMin - 1
                binding.chart.axisLeft.axisMaximum = dataSet.yMax + 1
                binding.chart.fitScreen()
            }
        }

        (activity as AppCompatActivity).supportActionBar?.title = args.symbol
        return binding.root
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