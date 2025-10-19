package com.example.prodhackathonspb.di

import com.example.prodhackathonspb.network.AcceptInviteService
import com.example.prodhackathonspb.network.AddGroupService
import com.example.prodhackathonspb.network.CreateInviteService
import com.example.prodhackathonspb.network.DeclineGroupInviteService
import com.example.prodhackathonspb.network.GetInvitesService
import com.example.prodhackathonspb.network.GetUserGroupService
import com.example.prodhackathonspb.network.GetUserService
import com.example.prodhackathonspb.network.ServerStatusService
import com.example.prodhackathonspb.network.SignInService
import com.example.prodhackathonspb.network.SignUpService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://team-25-8gelp3kc.hack.prodcontest.ru/"

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        encodeDefaults = true
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideServerStatusService(retrofit: Retrofit): ServerStatusService {
        return retrofit.create(ServerStatusService::class.java)
    }

    @Provides
    @Singleton
    fun provideGetUserService(retrofit: Retrofit): GetUserService {
        return retrofit.create(GetUserService::class.java)
    }

    @Provides
    @Singleton
    fun provideSignUpService(retrofit: Retrofit): SignUpService {
        return retrofit.create(SignUpService::class.java)
    }

    @Provides
    @Singleton
    fun provideSignInService(retrofit: Retrofit): SignInService {
        return retrofit.create(SignInService::class.java)
    }

    @Provides
    @Singleton
    fun provideAcceptInviteService(retrofit: Retrofit): AcceptInviteService {
        return retrofit.create(AcceptInviteService::class.java)
    }

    @Provides
    @Singleton
    fun provideCreateInviteService(retrofit: Retrofit): CreateInviteService {
        return retrofit.create(CreateInviteService::class.java)
    }

    @Provides
    @Singleton
    fun provideGetInvitesService(retrofit: Retrofit): GetInvitesService {
        return retrofit.create(GetInvitesService::class.java)
    }

    @Provides
    @Singleton
    fun provideGetGroupsService(retrofit: Retrofit): GetUserGroupService {
        return retrofit.create(GetUserGroupService::class.java)
    }

    @Provides
    @Singleton
    fun provideAddGroupService(retrofit: Retrofit): AddGroupService {
        return retrofit.create(AddGroupService::class.java)
    }

    @Provides
    @Singleton
    fun provideDeclineGroupInviteService(retrofit: Retrofit): DeclineGroupInviteService {
        return retrofit.create(DeclineGroupInviteService::class.java)
    }
}
