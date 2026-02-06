package com.pankajgadge.impl.di

import com.pankajgadge.api.RetrofitQualifier
import com.pankajgadge.api.repository.QuizRepositoryApi
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Retrofit Flow
// ViewModel → Repository Interface → RetrofitRepositoryImpl → StudentApi → Backend Server

@Module
@InstallIn(SingletonComponent::class)
abstract class RetrofitRepositoryModule {

    @Binds
    @Singleton
    @RetrofitQualifier  // Custom qualifier
    abstract fun bindRetrofitQuizRepository(
        impl: RetrofitQuizRepositoryImpl
    ): QuizRepositoryApi
}