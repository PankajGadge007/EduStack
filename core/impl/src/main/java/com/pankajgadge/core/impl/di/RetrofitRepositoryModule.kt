package com.pankajgadge.core.impl.di

// Retrofit Flow
// ViewModel → Repository Interface → RetrofitRepositoryImpl → StudentApi → Backend Server

class RetrofitRepositoryModule {

}
//@Module
//@InstallIn(SingletonComponent::class)
//abstract class RetrofitRepositoryModule {
//
//    @Binds
//    @Singleton
//    @RetrofitQualifier  // Custom qualifier
//    abstract fun bindRetrofitQuizRepository(
//        impl: RetrofitQuizRepositoryImpl
//    ): QuizRepository
//}