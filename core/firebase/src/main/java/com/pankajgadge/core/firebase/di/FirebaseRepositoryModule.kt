package com.pankajgadge.core.firebase.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseRepositoryModule {


//    @Binds
//    @Singleton
//    abstract fun bindAuthRepository(
//        impl: FirebaseAuthRepositoryImpl
//    ): AuthRepository
}