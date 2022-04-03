package io.invest.app.util

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

val Json = Json { ignoreUnknownKeys = true }

@Serializable
data class AuthResponse(val success: Boolean, val token: String)