package com.example.prodhackathonspb.network

import com.example.prodhackathonspb.network.models.AcceptInviteBody
import com.example.prodhackathonspb.network.models.AcceptInviteResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AcceptInviteService {
    @POST("group_invite/accept")
    suspend fun acceptInvite(
        @Header("Authorization") token: String,
        @Body body: AcceptInviteBody
    ): AcceptInviteResponse
}
