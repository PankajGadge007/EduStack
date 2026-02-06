package com.pankajgadge.impl.network


import com.pankajgadge.api.logging.Logger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object OkHttpProvider {

    fun create(
        jwtInterceptor: JwtInterceptor,
        logger: Logger
    ): OkHttpClient {

        val loggingInterceptor = HttpLoggingInterceptor {
            logger.d(it,"")
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(jwtInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }
}
