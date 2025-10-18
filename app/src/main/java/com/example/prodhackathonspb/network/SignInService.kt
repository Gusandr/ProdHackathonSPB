package com.example.prodhackathonspb.network

import com.example.prodhackathonspb.network.models.SignRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface SignInService {
    @POST("user/sign_in")
    suspend fun signIn(@Body request: SignRequest): String
}