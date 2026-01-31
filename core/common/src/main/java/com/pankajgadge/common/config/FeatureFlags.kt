package com.pankajgadge.common.config

import dagger.Provides
import javax.inject.Singleton

// core-common/config/FeatureFlags.kt
object FeatureFlags {
    const val USE_FIREBASE = true  // Toggle here!
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