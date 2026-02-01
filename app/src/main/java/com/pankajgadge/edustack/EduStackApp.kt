package com.pankajgadge.edustack

import android.app.Application
import android.content.pm.ApplicationInfo
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class EduStackApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if (0 != (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE)) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
