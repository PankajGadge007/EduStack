package com.pankajgadge.core.impl.network

import retrofit2.Retrofit

interface NetworkClient {
    fun retrofit(){}
    fun <T> createService(serviceClass: Class<T>): T
    fun getRetrofitInstance(): Retrofit
}