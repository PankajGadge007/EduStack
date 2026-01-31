package com.pankajgadge.api.logging

interface Logger {
    fun d(message: String)
    fun e(throwable: Throwable, message: String)
}