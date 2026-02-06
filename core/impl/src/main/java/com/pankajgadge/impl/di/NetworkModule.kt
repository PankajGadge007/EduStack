package com.pankajgadge.impl.di


import com.pankajgadge.api.StudentApi
import com.pankajgadge.api.auth.TokenProvider
import com.pankajgadge.api.logging.Logger
import com.pankajgadge.impl.datasource.FakeStudentApi
import com.pankajgadge.impl.logging.TimberLogger
import com.pankajgadge.impl.network.JwtInterceptor
import com.pankajgadge.impl.network.OkHttpProvider
import com.pankajgadge.impl.network.RetrofitProvider
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
    fun provideStudentApi(): StudentApi = FakeStudentApi()

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