package io.invest.app.util

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

val Json = Json { ignoreUnknownKeys = true }

enum class TimeRange(val range: String) {
    WEEKS("2w"),
    MONTHS("3m"),
    YEAR("1y")
}

@Serializable
data class AuthResponse(val success: Boolean = false, val token: String = "")

@Serializable
data class Stock(val symbol: String, val name: String, val price: StockPrice? = null)

@Serializable
data class Asset(val quantity: Float, val stock: Stock)

@Serializable
data class Portfolio(val cash: Float, val value: Float, val assets: Map<String, Asset>)

@Serializable
data class PortfolioHistory(val date: String, val change: Float)

@Serializable
data class PortfolioHistoryResponse(val success: Boolean, val history: List<PortfolioHistory>)

@Serializable
data class StockListResponse(val success: Boolean, val stocks: List<Stock>)

@Serializable
data class StockPrice(
    val symbol: String,
    val close: Float,
    val high: Float,
    val low: Float,
    val open: Float,
    val volume: Int,
    val date: Instant,
    val updated: Instant
)

@Serializable
data class PriceResponse(val success: Boolean, val price: Float)

@Serializable
data class PriceHistoryResponse(val success: Boolean, val prices: List<StockPrice>)

@Serializable
data class PortfolioResponse(val success: Boolean = false, val portfolio: Portfolio)