package com.pankajgadge.api

import retrofit2.Retrofit

/**
 * Network client interface for creating API services.
 * This abstraction allows us to swap network implementations if needed.
 */
interface NetworkClient {
    /**
     * Creates an instance of the specified API service
     */
    fun <T> createService(serviceClass: Class<T>): T

    /**
     * Returns the underlying Retrofit instance for advanced use cases
     */
    fun getRetrofitInstance(): Retrofit
}