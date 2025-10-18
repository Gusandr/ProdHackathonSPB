package com.example.prodhackathonspb.network

import com.example.prodhackathonspb.network.models.User
import retrofit2.http.GET
import retrofit2.http.Header

interface GetUserService {
    @GET("user/get_me")
    suspend fun getUser(
        @Header("Authorization") token: String
    ): User
}