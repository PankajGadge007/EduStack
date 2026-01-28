package com.pankajgadge.edustack.security

//TODO use EncryptedSharedPreferences
interface SecureStore {
    fun saveToken(token: String)
    fun getToken(): String?
    fun clear()
}
