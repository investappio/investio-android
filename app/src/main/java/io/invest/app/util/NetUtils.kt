package io.invest.app.util

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

val Json = Json { ignoreUnknownKeys = true}

@Serializable
data class AuthResponse(val success: Boolean = false, val token: String = "")

@Serializable
data class Stock(val symbol: String, val name: String)

@Serializable
data class Asset(val quantity: Float, val stock: Stock)

@Serializable
data class Portfolio(val balance: Float, val assets: Map<String, Asset>)

@Serializable
data class StockListResponse(val success: Boolean, val stocks: List<Stock>)

@Serializable
data class StockPrice(val symbol: String, val close: Float, val high: Float, val low: Float, val open: Float, val volume: Int, val date: Instant, val updated: Instant)

@Serializable
data class StockPriceResponse(val success: Boolean, val prices: List<StockPrice>)

@Serializable
data class PortfolioResponse(val success: Boolean = false, val portfolio: Portfolio)