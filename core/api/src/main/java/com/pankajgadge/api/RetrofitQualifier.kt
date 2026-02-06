package com.pankajgadge.api

import javax.inject.Qualifier

/**
 * Qualifier annotation to distinguish Retrofit-based implementations
 * from other implementations (like Room or Fake).
 *
 * Usage:
 * @Binds
 * @RetrofitQualifier
 * abstract fun bindRetrofitQuizRepository(impl: RetrofitQuizRepositoryImpl): QuizRepository
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitQualifier