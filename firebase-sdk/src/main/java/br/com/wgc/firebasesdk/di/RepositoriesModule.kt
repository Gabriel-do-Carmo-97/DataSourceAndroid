package br.com.wgc.firebasesdk.di

import br.com.wgc.firebasesdk.data.repository.FirebaseAuthRepositoryImpl
import br.com.wgc.firebasesdk.domain.repository.FirebaseAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoriesModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: FirebaseAuthRepositoryImpl
    ): FirebaseAuthRepository

}