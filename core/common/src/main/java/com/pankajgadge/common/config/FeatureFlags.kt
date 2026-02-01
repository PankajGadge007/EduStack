package com.pankajgadge.common.config

import com.pankajgadge.api.repository.QuizRepository
import dagger.Provides
import javax.inject.Singleton

object FeatureFlags {
    const val USE_FIREBASE = true
    const val ENABLE_ANALYTICS = false
    const val ENABLE_CRASHLYTICS = false
}

// app/di/AppModule.kt
@Provides
@Singleton
fun provideQuizRepository(
    @FirebaseQualifier firebase: QuizRepository,
    @RetrofitQualifier retrofit: QuizRepository
): QuizRepository {
    return if (FeatureFlags.USE_FIREBASE) firebase else retrofit
}