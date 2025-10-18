package com.example.prodhackathonspb.di

import com.example.prodhackathonspb.network.GetUserService
import com.example.prodhackathonspb.network.PostUserService
import com.example.prodhackathonspb.network.ServerStatusService
import com.example.prodhackathonspb.network.SignUpService
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
    fun provideRepository(
        service: ServerStatusService,
        serviceGetUser: GetUserService,
        serviceSignUpService: SignUpService
    ): Repository {
        return Repository(service, serviceGetUser, serviceSignUpService)
    }

    @Provides
    @Singleton
    fun provideGetUser(retrofit: Retrofit): GetUserService {
        return retrofit.create(GetUserService::class.java)
    }

    @Provides
    @Singleton
    fun providePostUser(retrofit: Retrofit): PostUserService {
        return retrofit.create(PostUserService::class.java)
    }

    @Provides
    @Singleton
    fun provideSignUp(retrofit: Retrofit): SignUpService {
        return retrofit.create(SignUpService::class.java)
    }
}