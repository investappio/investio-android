package io.invest.app.net

import android.util.Log
import io.invest.app.util.AuthResponse
import io.invest.app.util.Json
import io.invest.app.util.StockPriceResponse
import io.invest.app.util.StockSearchResponse
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

    suspend fun searchStocks(query: String): StockSearchResponse? {
        val url =
            "$BASE_URL/stocks/search".toHttpUrl().newBuilder().addQueryParameter("query", query)
                .build()

        val req = Request.Builder().url(url).get()

        return withContext(Dispatchers.IO) {
            req.json()?.let { Json.decodeFromString<StockSearchResponse>(it) }
        }
    }

    suspend fun getPrices(query: String): StockPriceResponse? {
        val url =
            "$BASE_URL/stocks/price".toHttpUrl().newBuilder().addQueryParameter("query", query)
                .build()

        val req = Request.Builder().url(url).get()

        return withContext(Dispatchers.IO) {
            req.json()?.let { Json.decodeFromString<StockPriceResponse>(it) }
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