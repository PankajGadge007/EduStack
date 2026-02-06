package com.pankajgadge.api.logging

interface Logger {
    fun d(tag: String, message: String)
    fun e(tag: String,throwable: Throwable, message: String)
}