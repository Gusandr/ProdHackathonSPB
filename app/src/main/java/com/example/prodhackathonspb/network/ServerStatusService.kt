package com.example.prodhackathonspb.network

import retrofit2.http.GET


interface ServerStatusService {
    @GET("status")
    suspend fun getStatus()
}