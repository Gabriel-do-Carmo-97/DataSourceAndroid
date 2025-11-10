package br.com.wgc.firebasesdk.di

import br.com.wgc.firebasesdk.data.repository.FirebaseAuthRepositoryImpl
import br.com.wgc.firebasesdk.data.repository.FirebaseDatabaseRepositoryImpl
import br.com.wgc.firebasesdk.data.repository.FirebaseFirestoreRepositoryImpl
import br.com.wgc.firebasesdk.data.repository.GeoRepositoryImpl
import br.com.wgc.firebasesdk.data.repository.MessageRepositoryImpl
import br.com.wgc.firebasesdk.data.repository.PresenceRepositoryImpl
import br.com.wgc.firebasesdk.domain.repository.FirebaseAuthRepository
import br.com.wgc.firebasesdk.domain.repository.FirebaseDatabaseRepository
import br.com.wgc.firebasesdk.domain.repository.FirebaseFirestoreRepository
import br.com.wgc.firebasesdk.domain.repository.GeoRepository
import br.com.wgc.firebasesdk.domain.repository.MessageRepository
import br.com.wgc.firebasesdk.domain.repository.PresenceRepository
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

    @Binds
    @Singleton
    abstract fun bindsGeoRepository(
        geoRepositoryImpl: GeoRepositoryImpl
    ): GeoRepository

    @Binds
    @Singleton
    abstract fun bindMessageRepository(
        messageRepositoryImpl: MessageRepositoryImpl
    ): MessageRepository

    @Binds
    @Singleton
    abstract fun bindPresenceRepository(
        presenceRepositoryImpl: PresenceRepositoryImpl
    ): PresenceRepository

    @Binds
    @Singleton
    abstract fun bindRealtimeDatabaseRepository(
        databaseRepositoryImpl: FirebaseDatabaseRepositoryImpl
    ): FirebaseDatabaseRepository

    @Binds
    @Singleton
    abstract fun bindStorageRepository(
        storageRepositoryImpl: FirebaseFirestoreRepositoryImpl
    ): FirebaseFirestoreRepository

}