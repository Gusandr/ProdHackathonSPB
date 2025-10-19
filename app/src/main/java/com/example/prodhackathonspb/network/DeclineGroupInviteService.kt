package com.example.prodhackathonspb.network

import com.example.prodhackathonspb.network.models.AcceptInviteBody
import com.example.prodhackathonspb.network.models.CreateInviteBody
import com.example.prodhackathonspb.network.models.CreateInviteResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface DeclineGroupInviteService {
    @POST("group_invite/decline")
    suspend fun declineGroup(@Header("Authorization") token: String, @Body body: AcceptInviteBody): Unit
}