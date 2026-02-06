package com.pankajgadge.impl

import android.util.Log
import com.pankajgadge.api.logging.Logger

import javax.inject.Inject

//todo Use Timber or Android Log for now.

class AndroidLogger @Inject constructor() : Logger {
    override fun d(tag: String, msg: String) {
        Log.d(tag, msg)
    }

    override fun e(tag: String, t: Throwable, msg: String) {
        Log.e(tag, msg, t)
    }
}