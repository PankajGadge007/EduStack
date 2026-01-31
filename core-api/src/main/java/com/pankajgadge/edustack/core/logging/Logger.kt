package com.pankajgadge.edustack.core.logging

interface Logger {
    fun d(message: String)
    fun e(throwable: Throwable, message: String)
}