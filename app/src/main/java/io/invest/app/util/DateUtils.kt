package io.invest.app.util

import android.content.Context
import androidx.core.os.ConfigurationCompat.getLocales
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*


fun yearDateFormat (locale: Locale) = DateTimeFormatter.ofPattern("MMM d, yyyy", locale)

enum class TimeRange(val range: String) {
    WEEKS("2w"),
    MONTHS("3m"),
    YEAR("1y")
}

fun Instant.format(formatter: DateTimeFormatter): String =
    this.toLocalDateTime(kotlinx.datetime.TimeZone.UTC).toJavaLocalDateTime()
        .format(formatter)

fun Instant.formatLocal(formatter: DateTimeFormatter): String =
    this.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).toJavaLocalDateTime()
        .format(formatter)
