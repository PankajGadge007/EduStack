package com.pankajgadge.impl



import com.pankajgadge.api.DispatcherProvider
import com.pankajgadge.api.NetworkClient
import com.pankajgadge.api.logging.Logger
import com.pankajgadge.api.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreBindings {
//    @Binds
//    abstract fun bindAuthRepo(impl: FakeAuthRepository): AuthRepository
    @Binds abstract fun bindLogger(impl: AndroidLogger): Logger
    @Binds abstract fun bindDispatchers(impl: DefaultDispatcherProvider): DispatcherProvider
    @Binds abstract fun bindNetwork(impl: RetrofitNetworkClient): NetworkClient
}
