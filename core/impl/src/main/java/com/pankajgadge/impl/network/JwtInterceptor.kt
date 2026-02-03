package com.pankajgadge.impl.network

import com.pankajgadge.api.auth.TokenProvider
import okhttp3.Interceptor
import okhttp3.Response

class JwtInterceptor(
    private val tokenProvider: TokenProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = tokenProvider.getToken()

        val request = original.newBuilder().apply {
            if (!token.isNullOrEmpty()) {
                addHeader("Authorization", "Bearer $token")
            }
        }.build()

        return chain.proceed(request)
    }
}
