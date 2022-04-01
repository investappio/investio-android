package io.invest.app.net

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val BASE_URL = "10.0.2.2:3000"
private const val TAG = "Investio"

@Singleton
class Investio @Inject constructor(private val client: OkHttpClient) {
    suspend fun login(username: String, password: String): String? {
        val url = "http://$BASE_URL/auth/login"

        val body = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .build()

        val req = Request.Builder().url(url).post(body)

        return withContext(Dispatchers.IO) {
            req.json()
        }
    }

    suspend fun register(
        name: String,
        email: String,
        dob: Int,
        username: String,
        password: String,
        phone: String? = null,
    ): String? {
        val url = "http://$BASE_URL/auth/register"

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
            req.json()
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