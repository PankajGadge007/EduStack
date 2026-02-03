package com.pankajgadge.firebase.di

import com.pankajgadge.api.repository.AuthRepository
import com.pankajgadge.api.repository.QuizRepository
import com.pankajgadge.firebase.repository.FirebaseAuthRepositoryImpl
import com.pankajgadge.firebase.repository.FirebaseQuizRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FirebaseQuiz

@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseRepositoryModule {

    @Binds
    @Singleton
    @FirebaseQuiz
    abstract fun bindFirebaseQuizRepository(
        impl: FirebaseQuizRepositoryImpl
    ): QuizRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: FirebaseAuthRepositoryImpl
    ): AuthRepository
}