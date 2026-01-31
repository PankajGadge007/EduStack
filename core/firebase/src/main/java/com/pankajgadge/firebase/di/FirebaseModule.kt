package com.pankajgadge.firebase.di

/*
- Provides Firebase SDK instances
*/
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// SingletonComponent = lives for entire app lifecycle
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    /**
     * Provides FirebaseAuth instance for authentication
     * Used for: Google Sign-in, user management
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
}