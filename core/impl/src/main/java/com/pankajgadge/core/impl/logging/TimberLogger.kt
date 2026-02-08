package com.pankajgadge.core.impl.logging

import com.pankajgadge.core.api.logging.Logger
import timber.log.Timber

class TimberLogger : Logger {

    override fun d(tag: String, message: String) {
        Timber.d(message)
    }

    override fun e(tag: String, throwable: Throwable, message: String) {
        Timber.e(throwable, message)
    }
}
