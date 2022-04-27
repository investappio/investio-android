package io.invest.app.net

import android.util.Log
import io.invest.app.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.decodeFromString
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val BASE_URL = "http://10.0.2.2:3000"
private const val TAG = "Investio"

@Singleton
class Investio @Inject constructor(private val client: OkHttpClient) {
    suspend fun login(username: String, password: String): AuthResponse? {
        val url = "$BASE_URL/auth/login"

        val body = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .build()

        val req = Request.Builder().url(url).post(body)

        return withContext(Dispatchers.IO) {
            req.json()?.let { Json.decodeFromString<AuthResponse>(it) }
        }
    }

    suspend fun register(
        name: String,
        email: String,
        dob: Long,
        username: String,
        password: String,
        phone: String? = null,
    ): AuthResponse? {
        val url = "$BASE_URL/auth/register"

        val body = FormBody.Builder().apply {
            add("name", name)

            phone?.let {
                add("phone", phone)
            }

            add("email", email)
            add("dob", dob.toString())
            add("username", username)
            add("password", password)
        }.build()

        val req = Request.Builder().url(url).post(body)

        return withContext(Dispatchers.IO) {
            req.json()?.let { Json.decodeFromString<AuthResponse>(it) }
        }
    }

    suspend fun movers(count: Int): MoversResponse? {
        val url =
            "$BASE_URL/assets/movers".toHttpUrl().newBuilder()
                .addQueryParameter("count", count.toString())
                .build()

        val req = Request.Builder().url(url).get()

        return withContext(Dispatchers.IO) {
            req.json()?.let { Json.decodeFromString<MoversResponse>(it) }
        }
    }

    suspend fun searchAssets(query: String): AssetListResponse? {
        val url =
            "$BASE_URL/assets/search".toHttpUrl().newBuilder().addQueryParameter("query", query)
                .build()

        val req = Request.Builder().url(url).get()

        return withContext(Dispatchers.IO) {
            req.json()?.let { Json.decodeFromString<AssetListResponse>(it) }
        }
    }

    suspend fun getAsset(
        symbol: String
    ): AssetResponse? {
        val url = "$BASE_URL/assets/${symbol}".toHttpUrl()

        val req = Request.Builder().url(url).get()

        return withContext(Dispatchers.IO) {
            req.json()?.let { Json.decodeFromString<AssetResponse>(it) }
        }
    }

    suspend fun getQuote(
        symbol: String
    ): QuoteResponse? {
        val url = "$BASE_URL/assets/${symbol}/quote".toHttpUrl()

        val req = Request.Builder().url(url).get()

        return withContext(Dispatchers.IO) {
            req.json()?.let { Json.decodeFromString<QuoteResponse>(it) }
        }
    }

    suspend fun getQuotes(
        vararg symbols: String
    ): MultiQuoteResponse? {
        val url = "$BASE_URL/assets/quotes".toHttpUrl().newBuilder()
            .addQueryParameter("symbols", symbols.joinToString(",")).build()

        val req = Request.Builder().url(url).get()

        return withContext(Dispatchers.IO) {
            req.json()?.let { Json.decodeFromString<MultiQuoteResponse>(it) }
        }
    }

    suspend fun getOrders(
        time: Instant = Clock.System.now(),
        count: Int = 5
    ): OrderResponse? {
        val url = "$BASE_URL/user/orders".toHttpUrl().newBuilder()
            .addQueryParameter("start", time.toEpochMilliseconds().toString())
            .addQueryParameter("count", count.toString()).build()

        val req = Request.Builder().url(url).get()

        return withContext(Dispatchers.IO) {
            req.json()?.let { Json.decodeFromString<OrderResponse>(it) }
        }
    }

    suspend fun getPriceHistory(
        symbol: String,
        timeRange: TimeRange = TimeRange.WEEKS,
    ): PriceHistoryResponse? {
        val url = "$BASE_URL/assets/${symbol}/price/historical/${timeRange.range}"

        val req = Request.Builder().url(url).get()

        return withContext(Dispatchers.IO) {
            req.json()?.let { Json.decodeFromString<PriceHistoryResponse>(it) }
        }
    }

    suspend fun getPortfolio(): PortfolioResponse? {
        val url = "$BASE_URL/user/portfolio".toHttpUrl()

        val req = Request.Builder().url(url).get()

        return withContext(Dispatchers.IO) {
            req.json()?.let { Json.decodeFromString<PortfolioResponse>(it) }
        }
    }

    suspend fun getPortfolioHistory(timeRange: TimeRange = TimeRange.WEEKS): PortfolioHistoryResponse? {
        val url = "$BASE_URL/user/portfolio/historical/${timeRange.range}"

        val req = Request.Builder().url(url).get()

        return withContext(Dispatchers.IO) {
            req.json()?.let { Json.decodeFromString<PortfolioHistoryResponse>(it) }
        }
    }

    suspend fun getProfile(): ProfileResponse? {
        val url = "$BASE_URL/user/profile"

        val req = Request.Builder().url(url).get()

        return withContext(Dispatchers.IO) {
            req.json()?.let { Json.decodeFromString<ProfileResponse>(it) }
        }
    }

    suspend fun getLeaderboard(start: Int = 0, count: Int = 25): LeaderboardResponse? {
        val url = "$BASE_URL/user/leaderboard".toHttpUrl().newBuilder()
            .addQueryParameter("start", start.toString())
            .addQueryParameter("count", count.toString())
            .build()

        val req = Request.Builder().url(url).get()

        return withContext(Dispatchers.IO) {
            req.json()?.let { Json.decodeFromString<LeaderboardResponse>(it) }
        }
    }

    suspend fun getNews(start: Instant? = null, count: Int = 25): NewsResponse? {
        val url = "$BASE_URL/assets/news".toHttpUrl().newBuilder()
            .addQueryParameter("start", start?.toEpochMilliseconds().toString())
            .addQueryParameter("count", count.toString())
            .build()

        val req = Request.Builder().url(url).get()

        return withContext(Dispatchers.IO) {
            req.json()?.let { Json.decodeFromString<NewsResponse>(it) }
        }
    }

    suspend fun buyAsset(symbol: String, amount: Float, type: ValueType): SuccessResponse? {
        val url = "$BASE_URL/assets/${symbol}/buy".toHttpUrl()

        val body = FormBody.Builder().add(type.key, amount.toString()).build()

        val req = Request.Builder().url(url).post(body)

        return withContext(Dispatchers.IO) {
            req.json()?.let { Json.decodeFromString<SuccessResponse>(it) }
        }
    }

    suspend fun sellAsset(symbol: String, amount: Float, type: ValueType): SuccessResponse? {
        val url = "$BASE_URL/assets/${symbol}/sell".toHttpUrl()

        val body = FormBody.Builder().add(type.key, amount.toString()).build()

        val req = Request.Builder().url(url).post(body)

        return withContext(Dispatchers.IO) {
            req.json()?.let { Json.decodeFromString<SuccessResponse>(it) }
        }
    }

    private fun Request.Builder.json(): String? {
        return try {
            val res = client.newCall(this.build()).execute()

            if (res.code != 200) {
                res.body?.close()
                return null
            }

            val body = res.body?.string()
            res.body?.close()

            body
        } catch (e: IOException) {
            Log.d(TAG, e.stackTraceToString())
            null
        }
    }
}