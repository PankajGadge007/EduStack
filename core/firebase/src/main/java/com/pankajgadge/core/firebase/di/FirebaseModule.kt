package com.pankajgadge.core.firebase.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.pankajgadge.core.api.repository.QuizRepository
import com.pankajgadge.core.domain.repository.AuthRepository
import com.pankajgadge.firebase.auth.FirebaseAuthRepositoryImpl
import com.pankajgadge.core.firebase.datasource.FirebaseAuthDataSource
import com.pankajgadge.firebase.repository.FirebaseQuizRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Hilt module for Firebase dependencies
 * SingletonComponent = lives for entire app lifecycle
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    /**
     * Provides FirebaseAuth instance for authentication
     * Used for: Google Sign-in, email/password auth, user management
     */
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
       return FirebaseAuth.getInstance()
    }

    /**
     * Provides FirebaseFirestore instance for database operations
     * Used for: Reading quizzes, saving submissions
     */
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    /**
     * Provides FirebaseStorage instance for file storage
     * Used for: Quiz images (future feature)
     */
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    /**
     * Provides FirebaseAuthDataSource
     * Used for: All direct Firebase Auth operations
     */
    @Provides
    @Singleton
    fun provideFirebaseAuthDataSource(
        firebaseAuth: FirebaseAuth
    ): FirebaseAuthDataSource {
        return FirebaseAuthDataSource(firebaseAuth)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FirebaseQuiz

/**
 * Separate module for binding interfaces to implementations
 * This must be an abstract class when using @Binds
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseBindingModule {

    @Binds
    @Singleton
    @FirebaseQuiz
    abstract fun bindFirebaseQuizRepository(
        impl: FirebaseQuizRepositoryImpl
    ): QuizRepository

    /**
     * Binds AuthRepository interface to FirebaseAuthRepositoryImpl
     * This tells Hilt to use FirebaseAuthRepositoryImpl whenever AuthRepository is injected
     */
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: FirebaseAuthRepositoryImpl
    ): AuthRepository
}