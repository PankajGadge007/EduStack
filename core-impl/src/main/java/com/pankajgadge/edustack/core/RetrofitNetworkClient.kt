package com.pankajgadge.edustack.core

import com.pankajgadge.edustack.security.SecureStore
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject

// TODO
//--Setup Retrofit + OkHttp
//--Add:
//  -Logging interceptor
//  -Fake Auth interceptor (adds token header if exists)

class RetrofitNetworkClient @Inject constructor(
    private val secureStore: SecureStore
) : NetworkClient {
    override fun retrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://fake.api/")
        .client(okHttp())
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
}