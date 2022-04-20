package io.invest.app.util

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

val Json = Json { ignoreUnknownKeys = true }

@Serializable
data class AuthResponse(val success: Boolean = false, val token: String = "")

@Serializable
data class Stock(val symbol: String, val name: String)

@Serializable
data class Portfolio(val cash: Float, val value: Float, val assets: Map<String, Float>)

@Serializable
data class PortfolioHistory(val timestamp: Instant, val value: Float, val cash: Float)

@Serializable
data class PortfolioHistoryResponse(val success: Boolean, val history: List<PortfolioHistory>)

@Serializable
data class StockListResponse(val success: Boolean, val stocks: List<Stock>)

@Serializable
data class StockResponse(val success: Boolean, val stock: Stock)

@Serializable
data class StockPrice(
    val symbol: String,
    val close: Float,
    val high: Float,
    val low: Float,
    val open: Float,
    val volume: Int,
    val average: Float,
    val change: Float,
    val timestamp: Instant
)

@Serializable
data class QuoteResponse(val success: Boolean, val quote: Float)

@Serializable
data class PriceHistoryResponse(val success: Boolean, val prices: List<StockPrice>)

@Serializable
data class PortfolioResponse(val success: Boolean = false, val portfolio: Portfolio)