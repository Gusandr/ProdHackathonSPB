package com.example.prodhackathonspb.di

import com.example.prodhackathonspb.network.ServerStatusService
import com.example.prodhackathonspb.repository.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {
    @Provides
    @Singleton
    fun provideServerStatus(retrofit: Retrofit): ServerStatusService {
        return retrofit.create(ServerStatusService::class.java)
    }

    @Provides
    @Singleton
    fun provideRepository(service: ServerStatusService): Repository {
        return Repository(service)
    }
}