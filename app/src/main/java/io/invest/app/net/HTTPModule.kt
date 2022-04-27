package io.invest.app.net

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.invest.app.LocalStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Cache
import okhttp3.OkHttpClient
import javax.inject.Singleton
import kotlin.math.pow

private const val HEADER_AUTHORIZATION = "Authorization"

@Module
@InstallIn(SingletonComponent::class)
class HTTPModule {
    @Provides
    fun provideCache(ctx: Application): Cache {
        val cacheSize = 10 * 2.0.pow(20.0)
        return Cache(ctx.cacheDir, cacheSize.toLong())
    }

    @Provides
    @Singleton
    fun provideClient(dataStore: LocalStore, cache: Cache): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor { chain ->
                chain.request().newBuilder()
                    .addHeader(
                        HEADER_AUTHORIZATION,
                        "Bearer ${runBlocking { dataStore.getApiToken().first() }}"
                    )
                    .build()
                    .let(chain::proceed)
            }
            .build()
    }
}