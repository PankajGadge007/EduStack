package com.pankajgadge.edustack.core.auth

interface TokenProvider {
    fun getToken(): String?
}
