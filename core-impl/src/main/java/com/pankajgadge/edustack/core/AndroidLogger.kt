package com.pankajgadge.edustack.core

//todo Use Timber or Android Log for now.

class AndroidLogger @Inject constructor() : Logger {
    override fun d(tag: String, msg: String) = Log.d(tag, msg)
    override fun e(tag: String, msg: String, t: Throwable?) =
        Log.e(tag, msg, t)
}