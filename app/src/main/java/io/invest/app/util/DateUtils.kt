package io.invest.app.util

import android.content.Context
import androidx.core.os.ConfigurationCompat.getLocales
import java.text.SimpleDateFormat
import java.util.*

fun Calendar.formatDate(context: Context, tz: TimeZone = TimeZone.getDefault()): String =
    SimpleDateFormat(
        "MMM d, yyyy",
        getLocales(context.resources.configuration).get(0)
    ).apply { timeZone = tz }.format(this.time)
