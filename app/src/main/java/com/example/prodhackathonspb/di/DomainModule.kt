package com.example.prodhackathonspb.di

import com.example.prodhackathonspb.network.GetUserService
import com.example.prodhackathonspb.network.SignInService
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
    fun provideRepository(
        service: ServerStatusService,
        serviceGetUser: GetUserService,
        serviceSignUpService: SignUpService,
        serviceSignInService: SignInService,
    ): Repository {
        return Repository(service, serviceGetUser, serviceSignUpService, serviceSignInService)
    }
}