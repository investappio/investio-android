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

    suspend fun topGainStocks(count: Int): StockListResponse? {
        val url =
            "$BASE_URL/stocks/gainers".toHttpUrl().newBuilder()
                .addQueryParameter("count", count.toString())
                .build()

        val req = Request.Builder().url(url).get()

        return withContext(Dispatchers.IO) {
            req.json()?.let { Json.decodeFromString<StockListResponse>(it) }
        }
    }

    suspend fun searchStocks(query: String): StockListResponse? {
        val url =
            "$BASE_URL/stocks/search".toHttpUrl().newBuilder().addQueryParameter("query", query)
                .build()

        val req = Request.Builder().url(url).get()

        return withContext(Dispatchers.IO) {
            req.json()?.let { Json.decodeFromString<StockListResponse>(it) }
        }
    }

    suspend fun getPrices(
        stock: String,
        date: Instant = Clock.System.now(),
        days: Int = 5,
        weeks: Int = 0,
        months: Int = 0,
        years: Int = 0
    ): StockPriceResponse? {
        val url =
            "$BASE_URL/stocks/${stock}/price".toHttpUrl().newBuilder()
                .addQueryParameter("date", date.toEpochMilliseconds().toString())
                .addQueryParameter("days", days.toString())
                .addQueryParameter("weeks", weeks.toString())
                .addQueryParameter("months", months.toString())
                .addQueryParameter("years", years.toString())
                .build()

        val req = Request.Builder().url(url).get()

        return withContext(Dispatchers.IO) {
            req.json()?.let { Json.decodeFromString<StockPriceResponse>(it) }
        }
    }

    suspend fun getPortfolio(): PortfolioResponse? {
        val url = "$BASE_URL/user/portfolio".toHttpUrl()

        val req = Request.Builder().url(url).get()

        return withContext(Dispatchers.IO) {
            req.json()?.let { Json.decodeFromString<PortfolioResponse>(it) }
        }
    }

    suspend fun buyStock(stock: String, amount: Float): PortfolioResponse? {
        val url = "$BASE_URL/stocks/${stock}/buy".toHttpUrl()

        val body = FormBody.Builder().add("quantity", amount.toString()).build()

        val req = Request.Builder().url(url).post(body)

        return withContext(Dispatchers.IO) {
            req.json()?.let { Json.decodeFromString<PortfolioResponse>(it) }
        }
    }

    suspend fun sellStock(stock: String, amount: Float): PortfolioResponse? {
        val url = "$BASE_URL/stocks/${stock}/sell".toHttpUrl()

        val body = FormBody.Builder().add("quantity", amount.toString()).build()

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