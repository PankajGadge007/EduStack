package com.pankajgadge.edustack.core.di

import com.pankajgadge.edustack.core.auth.TokenProvider
import com.pankajgadge.edustack.core.logging.Logger
import com.pankajgadge.edustack.core.logging.TimberLogger
import com.pankajgadge.edustack.core.network.JwtInterceptor
import com.pankajgadge.edustack.core.network.OkHttpProvider
import com.pankajgadge.edustack.core.network.RetrofitProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLogger(): Logger = TimberLogger()

    @Provides
    @Singleton
    fun provideTokenProvider(): TokenProvider {
        return object : TokenProvider {
            override fun getToken(): String? = null
        }
    }

    @Provides
    @Singleton
    fun provideJwtInterceptor(
        tokenProvider: TokenProvider
    ): JwtInterceptor = JwtInterceptor(tokenProvider)

    @Provides
    @Singleton
    fun provideOkHttp(
        jwtInterceptor: JwtInterceptor,
        logger: Logger
    ): OkHttpClient = OkHttpProvider.create(jwtInterceptor, logger)

    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient
    ): Retrofit = RetrofitProvider.create(
        baseUrl = "https://api.edustack.com/",
        client = client
    )
}
