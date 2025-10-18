package com.example.prodhackathonspb.network

import com.example.prodhackathonspb.network.models.SignUpRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface SignUpService {
    @POST("user/sign_up")
    suspend fun signUp(@Body request: SignUpRequest): String
}