package io.invest.app.net

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Investio @Inject constructor(private val client: OkHttpClient) {
    val BASE_URL = "10.0.2.2:3000"

    suspend fun login(username: String, password: String): String? {
        val url = "http://$BASE_URL/auth/login"

        val body = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .build()

        val req = Request.Builder().url(url).post(body).build()

        return withContext(Dispatchers.IO) {
            val res = client.newCall(req).execute()

            if (res.code != 200) {
                res.body?.close()
                return@withContext null
            }

            val body = res.body?.string()

            res.body?.close()

            body
        }
    }

    suspend fun register(
        name: String,
        phone: String,
        email: String,
        dob: Int,
        username: String,
        password: String
    ): String? {
        val url = "http://$BASE_URL/auth/register"

        val body = FormBody.Builder()
            .add("name", name)
            .add("phone", phone)
            .add("email", email)
            .add("dob", dob.toString())
            .add("username", username)
            .add("password", password)
            .build()

        val req = Request.Builder().url(url).post(body).build()

        return withContext(Dispatchers.IO) {
            val res = client.newCall(req).execute()

            if (res.code != 200) {
                res.body?.close()
                return@withContext null
            }

            val body = res.body?.string()

            res.body?.close()

            body
        }
    }
}