package com.example.prodhackathonspb.network

import com.example.prodhackathonspb.network.models.CreateInviteBody
import com.example.prodhackathonspb.network.models.CreateInviteResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface CreateInviteService {
    @POST("/invite/create")
    suspend fun createInvite(@Body body: CreateInviteBody): CreateInviteResponse
}