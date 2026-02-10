package com.pankajgadge.core.firebase.di

import com.pankajgadge.core.domain.repository.AuthSessionRepository
import com.pankajgadge.core.firebase.auth.FirebaseAuthSessionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindAuthSessionRepository(impl: FirebaseAuthSessionRepository): AuthSessionRepository
}