package com.pankajgadge.api.auth

interface TokenProvider {
    fun getToken(): String?
}
