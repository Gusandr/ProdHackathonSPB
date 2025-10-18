package com.example.prodhackathonspb.network

import retrofit2.http.GET


interface ServerStatusService {
    @GET("healthcheck")
    suspend fun getStatus()
}