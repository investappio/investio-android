package io.invest.app.net

import android.util.Log
import io.invest.app.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    suspend fun topGainassets(count: Int): StockListResponse? {
        val url =
            "$BASE_URL/assets/gainers".toHttpUrl().newBuilder()
                .addQueryParameter("count", count.toString())
                .build()

        val req = Request.Builder().url(url).get()

        return withContext(Dispatchers.IO) {
            req.json()?.let { Json.decodeFromString<StockListResponse>(it) }
        }
    }

    suspend fun searchAssets(query: String): StockListResponse? {
        val url =
            "$BASE_URL/assets/search".toHttpUrl().newBuilder().addQueryParameter("query", query)
                .build()

        val req = Request.Builder().url(url).get()

        return withContext(Dispatchers.IO) {
            req.json()?.let { Json.decodeFromString<StockListResponse>(it) }
        }
    }

    suspend fun getStock(
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
    ): QuoteResponse? {
        val url = "$BASE_URL/assets/quotes?symbols=${symbols.joinToString()}".toHttpUrl()

        val req = Request.Builder().url(url).get()

        return withContext(Dispatchers.IO) {
            req.json()?.let { Json.decodeFromString<QuoteResponse>(it) }
        }
    }

    suspend fun getPriceHistory(
        symbol: String,
        timeRange: TimeRange = TimeRange.WEEKS,
    ): PriceHistoryResponse? {
        val url =
            "$BASE_URL/assets/${symbol}/price/historical/${timeRange.range}".toHttpUrl()

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

    suspend fun buyStock(symbol: String, amount: Float): PortfolioResponse? {
        val url = "$BASE_URL/assets/${symbol}/buy".toHttpUrl()

        val body = FormBody.Builder().add("qty", amount.toString()).build()

        val req = Request.Builder().url(url).post(body)

        return withContext(Dispatchers.IO) {
            req.json()?.let { Json.decodeFromString<PortfolioResponse>(it) }
        }
    }

    suspend fun sellStock(symbol: String, amount: Float): PortfolioResponse? {
        val url = "$BASE_URL/assets/${symbol}/sell".toHttpUrl()

        val body = FormBody.Builder().add("qty", amount.toString()).build()

        val req = Request.Builder().url(url).post(body)

        return withContext(Dispatchers.IO) {
            req.json()?.let { Json.decodeFromString<PortfolioResponse>(it) }
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