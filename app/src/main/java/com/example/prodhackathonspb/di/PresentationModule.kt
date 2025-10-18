package com.example.prodhackathonspb.di

import android.content.Context
import com.example.prodhackathonspb.login.data.TokenHolder
import com.example.prodhackathonspb.network.ServerStatusService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PresentationModule {
    @Provides
    @Singleton
    fun provideTokenHolder(@ApplicationContext context: Context): TokenHolder {
        return TokenHolder(context)
    }
}