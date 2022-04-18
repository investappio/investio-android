package io.invest.app.util

import android.content.Context
import androidx.core.os.ConfigurationCompat.getLocales
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

enum class TimeRange(val range: String) {
    WEEKS("2w"),
    MONTHS("3m"),
    YEAR("1y")
}

fun Calendar.formatDate(context: Context, tz: TimeZone = TimeZone.getDefault()): String =
    SimpleDateFormat(
        "MMM d, yyyy",
        getLocales(context.resources.configuration).get(0)
    ).apply { timeZone = tz }.format(this.time)

fun Instant.format(formatter: DateTimeFormatter): String =
    this.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).toJavaLocalDateTime()
        .format(formatter)
