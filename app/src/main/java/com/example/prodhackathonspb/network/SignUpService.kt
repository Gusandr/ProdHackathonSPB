package com.example.prodhackathonspb.network

import com.example.prodhackathonspb.network.models.SignRequest
import com.example.prodhackathonspb.network.models.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SignUpService {
    @POST("user/sign_up")
    suspend fun signUp(@Body request: SignRequest): Response<TokenResponse>
}