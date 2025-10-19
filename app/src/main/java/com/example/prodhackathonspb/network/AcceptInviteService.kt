package com.example.prodhackathonspb.network

import com.example.prodhackathonspb.network.models.AcceptInviteBody
import com.example.prodhackathonspb.network.models.AcceptInviteResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AcceptInviteService {
    @POST("/invite/accept")
    suspend fun acceptInvite(@Body body: AcceptInviteBody): AcceptInviteResponse
}