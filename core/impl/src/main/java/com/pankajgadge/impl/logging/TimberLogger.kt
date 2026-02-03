package com.pankajgadge.impl.logging

import com.pankajgadge.api.logging.Logger
import timber.log.Timber

class TimberLogger : Logger {

    override fun d(message: String) {
        Timber.d(message)
    }

    override fun e(throwable: Throwable, message: String) {
        Timber.e(throwable, message)
    }
}
