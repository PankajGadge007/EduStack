package com.pankajgadge.impl.logging

import com.pankajgadge.api.logging.Logger
import timber.log.Timber

class TimberLogger : Logger {

    override fun d(tag: String, message: String) {
        Timber.d(message)
    }

    override fun e(tag: String, throwable: Throwable, message: String) {
        Timber.e(throwable, message)
    }
}
