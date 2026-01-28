package com.pankajgadge.edustack.core.logging

import timber.log.Timber

class TimberLogger : Logger {

    override fun d(message: String) {
        Timber.d(message)
    }

    override fun e(throwable: Throwable, message: String) {
        Timber.e(throwable, message)
    }
}
