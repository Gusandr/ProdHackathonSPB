package com.example.prodhackathonspb.network

import com.example.prodhackathonspb.network.models.AcceptInviteBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ShardInviteDeclineService {
    @POST("shard_invite/decline")
    suspend fun declineGroup(@Header("Authorization") token: String, @Body body: AcceptInviteBody)
}