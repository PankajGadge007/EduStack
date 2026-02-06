package com.pankajgadge.impl

import com.pankajgadge.api.NetworkClient
import com.pankajgadge.edustack.security.SecureStore
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

// TODO
//--Setup Retrofit + OkHttp
//--Add:
//  -Logging interceptor
//  -Fake Auth interceptor (adds token header if exists)

/**
 * Retrofit-based implementation of NetworkClient.
 * Provides centralized network configuration for the app.
 */
@Singleton
class RetrofitNetworkClient @Inject constructor() : NetworkClient {

    // TODO: Replace with your actual API base URL
    private val baseUrl = "https://your-api-base-url.com/api/"

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(createLoggingInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    override fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }

    override fun getRetrofitInstance(): Retrofit {
        return retrofit
    }

    /**
     * Creates HTTP logging interceptor for debugging network requests
     */
    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
}