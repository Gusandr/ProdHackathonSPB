package com.example.prodhackathonspb.di

import com.example.prodhackathonspb.login.data.TokenHolder
import com.example.prodhackathonspb.network.AcceptInviteService
import com.example.prodhackathonspb.network.AddGroupService
import com.example.prodhackathonspb.network.CreateInviteService
import com.example.prodhackathonspb.network.GetInvitesService
import com.example.prodhackathonspb.network.GetUserGroupService
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
        tokenHolder: TokenHolder,
        serviceGetUser: GetUserService,
        serviceSignUpService: SignUpService,
        serviceSignInService: SignInService,
        acceptInviteService: AcceptInviteService,
        createInviteService: CreateInviteService,
        getInvitesService: GetInvitesService,
        getUserGroupService: GetUserGroupService,
        addGroupService: AddGroupService,
    ): Repository {
        return Repository(
            tokenHolder,
            service,
            serviceGetUser,
            serviceSignUpService,
            serviceSignInService,
            acceptInviteService,
            createInviteService,
            getInvitesService,
            getUserGroupService,
            addGroupService
            )
    }
}