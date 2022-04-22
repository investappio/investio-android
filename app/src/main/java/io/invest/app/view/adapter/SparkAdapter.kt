package io.invest.app.view.adapter

import com.google.android.material.color.MaterialColors
import com.robinhood.spark.SparkAdapter
import com.robinhood.spark.SparkView
import io.invest.app.R

abstract class SparkAdapter<T>(val view: SparkView, val items: List<T>) : SparkAdapter() {
    override fun getCount(): Int = items.size

    override fun getItem(index: Int): T = items[index]

    abstract fun getValue(index: Int): Float

    override fun getY(index: Int): Float {
        val value = getValue(index)

        if (index == count - 1) {
            view.lineColor =
                if (getValue(index) < getValue(0)) {
                    MaterialColors.getColor(
                        view,
                        com.google.android.material.R.attr.colorError
                    )
                } else {
                    MaterialColors.getColor(view, R.attr.colorSuccess)
                }
        }

        return value
    }
}