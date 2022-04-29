package io.invest.app.util

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNames

val Json = Json { ignoreUnknownKeys = true }

enum class ValueType(val key: String) {
    NOTIONAL("notional"),
    QUANTITY("qty")
}

enum class Side() {
    BUY,
    SELL
}

@Serializable
data class AuthResponse(val success: Boolean = false, val token: String = "")

@Serializable
data class Asset(val symbol: String, val name: String, val company: Company? = null)

@Serializable
data class Portfolio(val cash: Float, val value: Float, val assets: Map<String, Float>)

@Serializable
data class ProfileResponse(val success: Boolean, val profile: Profile)

@Serializable
data class Profile(
    val name: String,
    val email: String,
    val dob: Instant,
    val username: String,
    val phone: String? = null
)

@Serializable
data class User(val name: String, val username: String)

@Serializable
data class PortfolioHistory(val timestamp: Instant, val value: Float, val cash: Float)

@Serializable
data class PortfolioHistoryResponse(val success: Boolean, val history: List<PortfolioHistory>)

@Serializable
data class LeaderboardItem(
    val timestamp: Instant,
    val value: Float,
    val cash: Float,
    val user: User
)

@Serializable
data class LeaderboardResponse(val success: Boolean, val leaderboard: List<LeaderboardItem>)

@Serializable
data class AssetListResponse(val success: Boolean, val assets: List<Asset>)

@Serializable
data class AssetResponse(val success: Boolean, val asset: Asset)

@Serializable
data class Order(
    val symbol: String,
    val qty: Float,
    val notional: Float,
    val side: String,
    val timestamp: Instant
)

@Serializable
data class OrderResponse(val success: Boolean, val orders: List<Order>)

@Serializable
data class AssetPrice(
    val symbol: String,
    val close: Float,
    val high: Float,
    val low: Float,
    val open: Float,
    val volume: Int,
    val average: Float,
    val change: Float,
    val timestamp: Instant,
    val changePercent: Float
)

@Serializable
data class News(
    val id: Int,
    val author: String,
    val timestamp: Instant,
    val headline: String,
    val url: String,
    val symbols: List<String>,
    val source: String
)

@Serializable
data class Company(
    val website: String,
    val description: String,
    @JsonNames("CEO") val ceo: String,
    val tags: List<String>
)

@Serializable
data class NewsResponse(val success: Boolean, val prev: Instant?, val news: List<News>)

@Serializable
data class MoversResponse(val success: Boolean, val assets: List<AssetPrice>)

@Serializable
data class SuccessResponse(val success: Boolean)

@Serializable
data class QuoteResponse(val success: Boolean, val quote: Float)

@Serializable
data class MultiQuoteResponse(val success: Boolean, val quotes: Map<String, Float>)

@Serializable
data class PriceHistoryResponse(val success: Boolean, val prices: List<AssetPrice>)

@Serializable
data class PortfolioResponse(val success: Boolean = false, val portfolio: Portfolio)

