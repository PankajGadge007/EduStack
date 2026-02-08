package com.pankajgadge.core.api.auth

interface TokenProvider {
    fun getToken(): String?
}
